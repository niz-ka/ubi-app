package com.example.boardgamecollector

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
            handler.post {
                val adapter = BoardGamesAdapter(this, games)
                recyclerView.adapter = adapter
            }
        }

    }
}