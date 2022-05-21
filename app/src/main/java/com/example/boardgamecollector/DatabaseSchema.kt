package com.example.boardgamecollector

object DatabaseSchema {
    object Settings {
        const val TABLE_NAME = "settings"
        const val COLUMN_NAME_KEY = "key" // TEXT PRIMARY KEY
        const val COLUMN_NAME_VALUE = "value" // TEXT
    }
}