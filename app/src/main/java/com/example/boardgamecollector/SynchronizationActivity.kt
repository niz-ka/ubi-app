package com.example.boardgamecollector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import java.lang.Exception
import java.lang.NullPointerException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


class SynchronizationActivity : NavigationActivity() {
    companion object {
        private const val TAG = "SynchronizationActivity"
    }

    private lateinit var synchronizationTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var synchronizationButton: Button

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

        val syncSetting = Setting.findOne(DatabaseSchema.Settings.KEY_SYNCHRONIZATION)
            ?: throw NullPointerException("Last sync not present in database")

        if (syncSetting.value != null) {
            synchronizationTextView.text = syncSetting.value
        }


        synchronizationButton.setOnClickListener {
            synchronizationButton.isEnabled = false
            progressBar.progress = 0
            synchronize()
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

    private fun synchronize() {
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            val username = Setting.findOne(DatabaseSchema.Settings.KEY_USERNAME)?.value

            try {
                val url =
                    URL("https://boardgamegeek.com/xmlapi2/collection?username=${username}&stats=1")
                var connection = url.openConnection() as HttpURLConnection

                while (connection.responseCode == 202 || connection.responseCode == 429) {
                    Log.e(TAG, "Status code: ${connection.responseCode}")
                    updateProgress(0)
                    connection.disconnect()

                    makeToast("You are in queue. Please wait.")

                    for (i in 1..15) {
                        Thread.sleep(1000)
                        updateProgress(i * 5)
                    }

                    connection = url.openConnection() as HttpURLConnection
                }

                updateProgress(75)

                var games: List<Game>
                connection.inputStream.use {
                    games = XmlParser(it).parseUserCollection()
                }

                Game.deleteAll()
                Game.insertMany(games)

                val date = Calendar.getInstance().time
                val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH)
                val formattedDate = formatter.format(date)

                Setting.insertOrUpdateOne(Setting(DatabaseSchema.Settings.KEY_SYNCHRONIZATION, formattedDate))
                runOnUiThread {
                    synchronizationTextView.text = formattedDate
                }

                updateProgress(100)
                Log.e(TAG, "Games Size: ${games.size}")

            } catch (exception: Exception) {
                Log.e(TAG, exception.printStackTrace().toString())
                updateProgress(0)
                makeToast("Error. Make sure you have internet connection.")
            } finally {
                runOnUiThread {
                    synchronizationButton.isEnabled = true
                }
            }

        }

    }
}


