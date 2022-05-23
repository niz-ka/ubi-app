package com.example.boardgamecollector

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val TAG = "DatabaseHelper"
            const val DATABASE_VERSION = 1
            const val DATABASE_NAME = "app.db"

            lateinit var db: DatabaseHelper

            private const val SQL_CREATE_SETTINGS =
                ("CREATE TABLE ${DatabaseSchema.Settings.TABLE_NAME} (" +
                        "${DatabaseSchema.Settings.COLUMN_NAME_KEY} TEXT NOT NULL UNIQUE," +
                        "${DatabaseSchema.Settings.COLUMN_NAME_VALUE} TEXT)")

            private const val SQL_DELETE_SETTINGS = "DROP TABLE IF EXISTS ${DatabaseSchema.Settings.TABLE_NAME}"

            private const val SQL_CREATE_GAMES =
                ("CREATE TABLE ${DatabaseSchema.Games.TABLE_NAME} (" +
                        "${DatabaseSchema.Games.COLUMN_NAME_ID} INTEGER PRIMARY KEY," +
                        "${DatabaseSchema.Games.COLUMN_NAME_TITLE} TEXT," +
                        "${DatabaseSchema.Games.COLUMN_NAME_YEAR} INTEGER," +
                        "${DatabaseSchema.Games.COLUMN_NAME_RANK} INTEGER," +
                        "${DatabaseSchema.Games.COLUMN_NAME_IMAGE} BLOB," +
                        "${DatabaseSchema.Games.COLUMN_NAME_TYPE} TEXT NOT NULL)")

            private const val SQL_DELETE_GAMES = "DROP TABLE IF EXISTS ${DatabaseSchema.Games.TABLE_NAME}"
        }

    override fun onCreate(db: SQLiteDatabase) {
        Log.i(TAG, "Creating database")
        db.execSQL(SQL_CREATE_SETTINGS)
        db.execSQL(SQL_CREATE_GAMES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.i(TAG, "Upgrading database")
        db.execSQL(SQL_DELETE_GAMES)
        db.execSQL(SQL_DELETE_SETTINGS)
        onCreate(db)
    }

}