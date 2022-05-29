package com.example.boardgamecollector

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : NavigationActivity() {

    private lateinit var gamesNumberTextView: TextView
    private lateinit var expansionsNumberTextView: TextView
    private lateinit var lastSyncTextView: TextView
    private lateinit var userHelloTextView: TextView
    private lateinit var clearDataButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        create()
        supportActionBar?.title = getString(R.string.home)

        DatabaseHelper.db = DatabaseHelper(this)

        // DB not exists - start Configuration Activity
        if (!getDatabasePath(DatabaseHelper.DATABASE_NAME).exists()) {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        gamesNumberTextView = findViewById(R.id.gamesNumber)
        expansionsNumberTextView = findViewById(R.id.expansionsNumber)
        lastSyncTextView = findViewById(R.id.lastSync)
        userHelloTextView = findViewById(R.id.userHello)
        clearDataButton = findViewById(R.id.clearDataButton)

        val username = Setting.findOne(DatabaseSchema.Settings.KEY_USERNAME)?.value
        val lastSync = Setting.findOne(DatabaseSchema.Settings.KEY_SYNCHRONIZATION)?.value
        val gamesNumber = Game.count(Game.Type.BOARD_GAME)
        val expansionsNumber = Game.count(Game.Type.BOARD_EXPANSION)

        gamesNumberTextView.text = gamesNumber.toString()
        expansionsNumberTextView.text = expansionsNumber.toString()
        lastSyncTextView.text = (lastSync ?: "-").toString()
        userHelloTextView.text = getString(R.string.userHello, username ?: "Stranger")

        clearDataButton.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.clearData))
            .setMessage(getString(R.string.removeWarning))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                DatabaseHelper.db.close()
                this.deleteDatabase(DatabaseHelper.DATABASE_NAME)
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.no), null)
            .setIcon(R.drawable.ic_baseline_warning_24)
            .show()
    }

}