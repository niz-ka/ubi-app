package com.example.boardgamecollector

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


class SynchronizationActivity : NavigationActivity() {
    companion object {
        private const val TAG = "SynchronizationActivity"
        private val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH)
    }

    private lateinit var synchronizationTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var synchronizationButton: Button
    private lateinit var removeSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_synchronization)
        create()

        Log.i(TAG, "Creating Activity")
        supportActionBar?.title = getString(R.string.synchronization)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        synchronizationTextView = findViewById(R.id.synchronizationTextView)
        progressBar = findViewById(R.id.progressBar)
        synchronizationButton = findViewById(R.id.synchronizationButton)
        removeSwitch = findViewById(R.id.removeSwitch)

        val syncSetting = Setting.findOne(DatabaseSchema.Settings.KEY_SYNCHRONIZATION)
            ?: throw NullPointerException("Last sync not present in database")

        if (syncSetting.value != null) {
            synchronizationTextView.text = syncSetting.value
        }


        synchronizationButton.setOnClickListener {
            synchronizationButton.isEnabled = false
            progressBar.progress = 0
            val removeNonExistent = removeSwitch.isChecked
            synchronize(removeNonExistent)
        }
    }

    private fun updateProgress(value: Int) {
        runOnUiThread {
            progressBar.progress = value
        }
    }

    private fun makeToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun connect(url: String): HttpURLConnection {
        val requestedUrl = URL(url)
        var connection = requestedUrl.openConnection() as HttpURLConnection

        while (connection.responseCode == 202 || connection.responseCode == 429) {
            Log.e(TAG, "Status code: ${connection.responseCode}")
            updateProgress(0)
            connection.disconnect()

            makeToast("You are in queue. Please wait.")

            for (i in 1..15) {
                Thread.sleep(1000)
                updateProgress(i * 5)
            }

            connection = requestedUrl.openConnection() as HttpURLConnection
        }

        return connection
    }

    private fun synchronize(removeNonExistent: Boolean) {
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            val username = Setting.findOne(DatabaseSchema.Settings.KEY_USERNAME)?.value

            try {
                var connection =
                    connect("https://boardgamegeek.com/xmlapi2/collection?username=${username}&stats=1")

                updateProgress(75)

                var allGames: List<Game>
                connection.inputStream.use {
                    allGames = XmlParser(it).parseUserCollection()
                }

                Log.d(TAG, "All games size: ${allGames.size}")

                connection =
                    connect("https://boardgamegeek.com/xmlapi2/collection?username=${username}&subtype=boardgameexpansion")

                var expansionIds: List<Long>
                connection.inputStream.use {
                    expansionIds = XmlParser(it).getExpansionIds()
                }

                var expansionCounter = 0

                for (game in allGames) {
                    if (game.id in expansionIds) {
                        game.type = Game.Type.BOARD_EXPANSION
                        ++expansionCounter
                    }
                }

                Log.d(TAG, "Expansions size: $expansionCounter")

                val lastSync = Setting.findOne(DatabaseSchema.Settings.KEY_SYNCHRONIZATION)?.value
                if(lastSync != null) {
                    val prevGames = Game.findAll(Game.Type.BOARD_GAME).filter { it.rank != null }
                    val ranks = prevGames.map {
                        Rank(it.id, formatter.parse(lastSync), it.rank)
                    }
                    Rank.insertMany(ranks)
                }

                if(removeNonExistent)
                    Game.deleteAll()
                Game.insertMany(allGames)

                val date = Calendar.getInstance().time
                val formattedDate = formatter.format(date)

                Setting.insertOrUpdateOne(
                    Setting(
                        DatabaseSchema.Settings.KEY_SYNCHRONIZATION,
                        formattedDate
                    )
                )

                runOnUiThread {
                    synchronizationTextView.text = formattedDate
                }

                updateProgress(100)

            } catch (exception: Exception) {
                Log.e(TAG, exception.printStackTrace().toString())
                updateProgress(0)
                makeToast("Error. Make sure you have internet connection.")
            } finally {
                runOnUiThread {
                    synchronizationButton.isEnabled = true
                    removeSwitch.isChecked = false
                }
            }

        }

    }
}


