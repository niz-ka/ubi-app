package com.example.boardgamecollector

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DatabaseHelper.db = DatabaseHelper(this)

        val setting = Setting("my_key_test", "my_value_test")
        Setting.insertOne(setting)

        val newSetting = Setting.findOne(setting.key)

        Log.i(TAG, newSetting.toString())
        DatabaseHelper.db.close()
    }
}