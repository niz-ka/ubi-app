package com.example.boardgamecollector

import android.content.Intent
import android.os.Bundle
import android.util.Log

class MainActivity : NavigationActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        create()
        Log.i(TAG, "Creating activity")

        supportActionBar?.title = getString(R.string.home)
        DatabaseHelper.db = DatabaseHelper(this)

        // DB not exists - start Configuration Activity
        if (!getDatabasePath(DatabaseHelper.DATABASE_NAME).exists()) {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
    }

}