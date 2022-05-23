package com.example.boardgamecollector

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.net.HttpURLConnection
import java.net.URL

class BoardGamesAdapter(
    val context: Context,
    val games: List<Game>
    )
    : RecyclerView.Adapter<BoardGamesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.recycler_view_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleTextView.text = games[position].title
        holder.yearTextView.text = games[position].year.toString()
        holder.rankTextView.text = games[position].rank.toString()
    }

    override fun getItemCount(): Int {
        return games.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val yearTextView: TextView = view.findViewById(R.id.yearTextView)
        val rankTextView: TextView = view.findViewById(R.id.rankTextView)
        val imageView: ImageView = view.findViewById(R.id.imageView)

    }
}