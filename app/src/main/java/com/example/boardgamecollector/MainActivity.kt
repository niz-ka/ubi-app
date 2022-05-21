package com.example.boardgamecollector

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.database.sqlite.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseSchema.Settings.COLUMN_NAME_KEY, "password")
        }
        val newRow = db.insert(DatabaseSchema.Settings.TABLE_NAME, null, values)
    }
}