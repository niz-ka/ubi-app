package com.example.boardgamecollector

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Xml
import android.view.ViewParent
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import java.lang.Exception
import java.lang.NullPointerException
import java.net.HttpURLConnection
import java.net.URL


class SynchronizationActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SynchronizationActivity"
    }

    private lateinit var synchronizationTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var synchronizationButton: Button
    private lateinit var debugTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_synchronization)
        Log.i(TAG, "Creating Activity")
        supportActionBar?.title = getString(R.string.synchronization)

        synchronizationTextView = findViewById(R.id.synchronizationTextView)
        progressBar = findViewById(R.id.progressBar)
        synchronizationButton = findViewById(R.id.synchronizationButton)
        debugTextView = findViewById(R.id.debugTextView)

        val syncSetting = Setting.findOne(DatabaseSchema.Settings.KEY_SYNCHRONIZATION)
            ?: throw NullPointerException("Last sync not present in database")

        if(syncSetting.value != null) {
           synchronizationTextView.text = syncSetting.value
        }

        synchronizationButton.setOnClickListener {
            @Suppress("DEPRECATION")
            SynchronizationTask().execute()
        }
    }

    @Suppress("DEPRECATION")
    private inner class SynchronizationTask : AsyncTask<Void, Int, Boolean>() {

        override fun doInBackground(vararg p0: Void?): Boolean {
           // val username = Setting.findOne(DatabaseSchema.Settings.KEY_USERNAME)?.value
            val username = "NecesDaSeIgras"

            try {
                val url = URL("https://boardgamegeek.com/xmlapi2/collection?username=${username}&stats=1")
                var connection = url.openConnection() as HttpURLConnection

                while(connection.responseCode == 202 || connection.responseCode == 429) {
                    Log.e(TAG, "Status code: ${connection.responseCode}")
                    connection.disconnect()
                    Thread.sleep(10000)
                    connection = url.openConnection() as HttpURLConnection
                }

                connection.inputStream.use {
                    val games = XmlParser.parseUserCollection(it)
                    Log.e(TAG, games.toString())
                }

            } catch (exception: Exception) {
                Log.e(TAG, exception.printStackTrace().toString())
                return false
            }

            return true
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            debugTextView.text = values[0].toString()
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
        }
    }
}