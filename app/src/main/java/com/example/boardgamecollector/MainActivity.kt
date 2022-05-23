package com.example.boardgamecollector

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : NavigationActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        create()
        Log.i(TAG,"Creating activity")

        supportActionBar?.title = getString(R.string.boardGames)
        DatabaseHelper.db = DatabaseHelper(this)

       // DB not exists - start Configuration Activity
       if(!getDatabasePath(DatabaseHelper.DATABASE_NAME).exists()) {
           val intent = Intent(this, ConfigurationActivity::class.java)
           startActivity(intent)
           finish()
       } else {
           recyclerView = findViewById(R.id.gamesRecyclerView)
           val adapter = BoardGamesAdapter(this, listOf())
           recyclerView.adapter = adapter
           recyclerView.layoutManager = LinearLayoutManager(this)
           makeRecyclerView()
       }
    }

    private fun makeRecyclerView() {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val games = Game.findAll()
            val bitmaps = mutableListOf<Bitmap>()

            for(game in games) {
                val url = URL(game.image)
                val connection = url.openConnection() as HttpURLConnection
                val inputStream = connection.inputStream
                val outputStream = ByteArrayOutputStream()
                inputStream.copyTo(outputStream)
                val arr = outputStream.toByteArray()
                bitmaps.add(BitmapFactory.decodeStream(inputStream))

                try {
                    inputStream.close()
                    connection.disconnect()
                } catch (exception: Exception) {
                    Log.e(TAG, "CLOSE EX: $exception")
                }
            }

            handler.post {
                val adapter = BoardGamesAdapter(this, games)
                recyclerView.adapter = adapter
            }
        }

    }
}