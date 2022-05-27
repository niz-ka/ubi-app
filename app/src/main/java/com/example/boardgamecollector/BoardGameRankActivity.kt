package com.example.boardgamecollector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class BoardGameRankActivity : NavigationActivity() {

    companion object {
        private const val TAG = "BoardGameRankActivity"
    }

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_game_rank)
        create()
        Log.i(TAG, "Creating activity")

        textView = findViewById(R.id.textView)

        val id = intent.getLongExtra("id", 0)
        textView.text = id.toString()
    }
}