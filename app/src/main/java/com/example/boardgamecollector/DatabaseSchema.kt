package com.example.boardgamecollector

object DatabaseSchema {
    object Settings {
        const val TABLE_NAME = "settings"
        const val COLUMN_NAME_KEY = "key" // TEXT NOT NULL UNIQUE
        const val COLUMN_NAME_VALUE = "value" // TEXT

        const val KEY_USERNAME = "username"
        const val KEY_SYNCHRONIZATION = "synchronization"
    }

    object Games {
        const val TABLE_NAME = "games"
        const val COLUMN_NAME_ID = "id" // INTEGER PRIMARY KEY
        const val COLUMN_NAME_TITLE = "title" // TEXT NOT NULL
        const val COLUMN_NAME_YEAR = "year" // INTEGER
        const val COLUMN_NAME_RANK = "rank" // INTEGER
        const val COLUMN_NAME_IMAGE = "image" // TEXT
        const val COLUMN_NAME_TYPE = "type" // TEXT NOT NULL
    }

    object Ranks {
        const val TABLE_NAME = "ranks"
        const val COLUMN_NAME_ID = "id" // INTEGER PRIMARY KEY
        const val COLUMN_NAME_DATE = "date" // TEXT NOT NULL
        const val COLUMN_NAME_RANK = "rank" // INTEGER NOT NULL
    }
}