package com.example.boardgamecollector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

class ConfigurationActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ConfigurationActivity"
    }

    private lateinit var submitButton: Button
    private lateinit var usernameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        Log.i(TAG, "Creating Activity")
        supportActionBar?.title = getString(R.string.configuration)

        submitButton = findViewById(R.id.submitButton)
        usernameEditText = findViewById(R.id.usernameEditText)

        submitButton.setOnClickListener {
            val username = usernameEditText.text.toString()

            Setting.insertOrUpdateOne(Setting(DatabaseSchema.Settings.KEY_USERNAME, username))
            Setting.insertOrUpdateOne(Setting(DatabaseSchema.Settings.KEY_SYNCHRONIZATION, null))
            Setting.insertOrUpdateOne(Setting(DatabaseSchema.Settings.KEY_GAMES_NUMBER, null))
            Setting.insertOrUpdateOne(Setting(DatabaseSchema.Settings.KEY_EXPANSIONS_NUMBER, null))

            Log.i(TAG, "Database created")

            val intent = Intent(this, SynchronizationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}