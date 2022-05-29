package com.example.boardgamecollector

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.Executors


class SynchronizationActivity : NavigationActivity() {

    companion object {
        private const val TAG = "SynchronizationActivity"
    }

    private lateinit var synchronizationTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var synchronizationButton: Button
    private lateinit var removeSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_synchronization)
        create()
        supportActionBar?.title = getString(R.string.synchronization)

        synchronizationTextView = findViewById(R.id.synchronizationTextView)
        progressBar = findViewById(R.id.progressBar)
        synchronizationButton = findViewById(R.id.synchronizationButton)
        removeSwitch = findViewById(R.id.removeSwitch)

        val syncSetting = Setting.findOne(DatabaseSchema.Settings.KEY_SYNCHRONIZATION)
        if (syncSetting?.value != null) {
            synchronizationTextView.text = syncSetting.value
        }

        synchronizationButton.setOnClickListener {
            synchronizationButton.isEnabled = false
            progressBar.progress = 0
            synchronize(removeSwitch.isChecked)
        }
    }

    private fun connect(url: String, progressStart: Int = 0): HttpURLConnection {
        val requestedUrl = URL(url)
        var connection = requestedUrl.openConnection() as HttpURLConnection

        while (connection.responseCode == 202 || connection.responseCode == 429) {
            Log.e(TAG, "Status code: ${connection.responseCode}")
            updateProgress(progressStart)
            connection.disconnect()

            makeToast("You are in queue. Please wait.")

            for (i in 1..15) {
                Thread.sleep(1000)
                updateProgress(progressStart + i)
            }

            connection = requestedUrl.openConnection() as HttpURLConnection
        }

        return connection
    }

    private fun synchronize(removeNonExistent: Boolean) {
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            val username = Setting.findOne(DatabaseSchema.Settings.KEY_USERNAME)?.value
            val url1 = String.format(App.collectionUrl, username)
            val url2 = String.format(App.expansionCollectionUrl, username)

            try {
                var connection = connect(url1)
                updateProgress(15)

                var allGames: List<Game>
                connection.inputStream.use {
                    val parser = XmlParser(it)
                    val size = parser.parseUserCollection()
                    var progress = 15
                    val step = 60.0 / size
                    var growth = step

                    for(i in 1..size) {
                        parser.nextConversion()
                        if(15 + growth.toInt() != progress) {
                            progress = 15 + growth.toInt()
                            updateProgress(progress)
                        }
                        growth += step
                    }
                    allGames = parser.getUserCollection()
                }
                updateProgress(75)

                Log.d(TAG, "All games size: ${allGames.size}")

                connection = connect(url2, 75)
                updateProgress(90)

                var expansionIds: List<Long>
                connection.inputStream.use {
                    expansionIds = XmlParser(it).getExpansionIds()
                }

                for (game in allGames) {
                    if (game.id in expansionIds)
                        game.type = Game.Type.BOARD_EXPANSION
                }

                val lastSync = Setting.findOne(DatabaseSchema.Settings.KEY_SYNCHRONIZATION)?.value
                if (lastSync != null) {
                    val prevGames = Game.findAll(Game.Type.BOARD_GAME).filter { it.rank != null }
                    val ranks = prevGames.map {
                        Rank(it.id, App.formatter.parse(lastSync), it.rank)
                    }
                    Rank.insertMany(ranks)
                }

                if (removeNonExistent)
                    Game.deleteAll()
                Game.insertMany(allGames)

                val date = Calendar.getInstance().time
                val formattedDate = App.formatter.format(date)

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
}


