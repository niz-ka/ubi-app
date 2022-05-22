package com.example.boardgamecollector

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import java.io.File

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG,"Creating activity")
        DatabaseHelper.db = DatabaseHelper(this)

       // DB not exists - start Configuration Activity
       if(!getDatabasePath(DatabaseHelper.DATABASE_NAME).exists()) {
           val intent = Intent(this, ConfigurationActivity::class.java)
           startActivity(intent)
           finish()
       } else {
           val intent = Intent(this, SynchronizationActivity::class.java)
           startActivity(intent)
           finish()
       }
    }
}