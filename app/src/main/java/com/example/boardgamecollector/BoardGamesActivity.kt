package com.example.boardgamecollector

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

class BoardGamesActivity : NavigationActivity() {
    companion object {
        private const val TAG = "BoardGamesActivity"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressCircle: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_games)
        create()
        Log.i(TAG, "Creating activity")

        supportActionBar?.title = getString(R.string.boardGames)

        recyclerView = findViewById(R.id.gamesRecyclerView)
        progressCircle = findViewById(R.id.progressCircle)

        val adapter = BoardGamesAdapter(this, listOf(), onClick)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        makeRecyclerView()
    }

    private val onClick: (game: Game) -> Unit = {
        val intent = Intent(this, BoardGameRankActivity::class.java)
        intent.putExtra("id", it.id)
        startActivity(intent)
    }

    private fun makeRecyclerView() {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val games = Game.findAll(Game.Type.BOARD_GAME)
            handler.post {
                progressCircle.visibility = View.INVISIBLE
                val adapter = BoardGamesAdapter(this, games, onClick)
                recyclerView.adapter = adapter
            }
        }

    }
}