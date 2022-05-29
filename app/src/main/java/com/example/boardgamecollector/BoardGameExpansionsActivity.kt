package com.example.boardgamecollector

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

class BoardGameExpansionsActivity : NavigationActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressCircle: ProgressBar
    private lateinit var adapter: BoardGamesAdapter
    private val games = mutableListOf<Game>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_games)
        create()
        supportActionBar?.title = getString(R.string.boardGameExpansions)

        recyclerView = findViewById(R.id.gamesRecyclerView)
        progressCircle = findViewById(R.id.progressCircle)

        adapter = BoardGamesAdapter(this, games) { }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        makeRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun makeRecyclerView() {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            games.addAll(Game.findAll(Game.Type.BOARD_EXPANSION))
            handler.post {
                progressCircle.visibility = View.INVISIBLE
                games.sortWith(compareBy(nullsLast()) { it.title })
                adapter.notifyDataSetChanged()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = MenuInflater(this)
        inflater.inflate(R.menu.sort_menu_expansion, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.titleAsc -> games.sortWith(compareBy(nullsLast()) { it.title })
            R.id.titleDesc -> games.sortWith(compareByDescending(nullsFirst()) { it.title })
            R.id.yearAsc -> games.sortWith(compareBy(nullsLast()) { it.year })
            R.id.yearDesc -> games.sortWith(compareByDescending(nullsFirst()) { it.year })
        }
        adapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}