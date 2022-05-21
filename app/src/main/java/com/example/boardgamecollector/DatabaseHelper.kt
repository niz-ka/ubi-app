package com.example.boardgamecollector

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val TAG = "DatabaseHelper"
            private const val DATABASE_VERSION = 1
            private const val DATABASE_NAME = "app.db"

            lateinit var db: DatabaseHelper

            private const val SQL_CREATE_SETTINGS =
                ("CREATE TABLE ${DatabaseSchema.Settings.TABLE_NAME} (" +
                        "${DatabaseSchema.Settings.COLUMN_NAME_KEY} TEXT NOT NULL UNIQUE," +
                        "${DatabaseSchema.Settings.COLUMN_NAME_VALUE} TEXT)")

            private const val SQL_DELETE_SETTINGS = "DROP TABLE IF EXISTS ${DatabaseSchema.Settings.TABLE_NAME}"
        }

    override fun onCreate(db: SQLiteDatabase) {
        Log.i(TAG, "Creating database")
        db.execSQL(SQL_CREATE_SETTINGS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.i(TAG, "Upgrading database")
        db.execSQL(SQL_DELETE_SETTINGS)
        onCreate(db)
    }

}