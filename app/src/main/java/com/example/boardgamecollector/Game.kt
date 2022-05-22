package com.example.boardgamecollector

import android.content.ContentValues
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull

class Game(
    val id: Long,
    val title: String?,
    val year: Int?,
    val rank: Int?,
    val image: String?,
    val type: Type
) {

    enum class Type(name: String) {
        BOARD_GAME("board_game"),
        BOARD_EXPANSION("board_expansion")
    }

    override fun toString(): String {
        return "Game(id=$id, title=$title, year=$year, rank=$rank, image=$image, type=$type)"
    }

    companion object {
        fun insertOne(game: Game): Long {
            val db = DatabaseHelper.db.writableDatabase
            val values = ContentValues()
            values.put(DatabaseSchema.Games.COLUMN_NAME_ID, game.id)
            values.put(DatabaseSchema.Games.COLUMN_NAME_TITLE, game.title)
            values.put(DatabaseSchema.Games.COLUMN_NAME_YEAR, game.year)
            values.put(DatabaseSchema.Games.COLUMN_NAME_RANK, game.rank)
            values.put(DatabaseSchema.Games.COLUMN_NAME_IMAGE, game.image)
            values.put(DatabaseSchema.Games.COLUMN_NAME_TYPE, game.type.toString())
            return db.insert(DatabaseSchema.Games.TABLE_NAME, null, values)
        }

        fun insertMany(games: List<Game>): List<Long> {
            val ids = mutableListOf<Long>()
            for (game in games)
                ids.add(insertOne(game))
            return ids
        }

        fun deleteAll(): Int {
            val db = DatabaseHelper.db.writableDatabase
            return db.delete(
                DatabaseSchema.Games.TABLE_NAME,
                null,
                null
            )
        }

        fun findAll(): List<Game> {
            val db = DatabaseHelper.db.readableDatabase
            val games = mutableListOf<Game>()

            val projection = arrayOf(
                DatabaseSchema.Games.COLUMN_NAME_ID,
                DatabaseSchema.Games.COLUMN_NAME_TITLE,
                DatabaseSchema.Games.COLUMN_NAME_RANK,
                DatabaseSchema.Games.COLUMN_NAME_TYPE,
                DatabaseSchema.Games.COLUMN_NAME_IMAGE,
                DatabaseSchema.Games.COLUMN_NAME_YEAR
            )


            val cursor = db.query(
                DatabaseSchema.Games.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
            )

            with(cursor) {

                val idColumn = cursor.getColumnIndex(DatabaseSchema.Games.COLUMN_NAME_ID)
                val titleColumn = cursor.getColumnIndex(DatabaseSchema.Games.COLUMN_NAME_TITLE)
                val rankColumn = cursor.getColumnIndex(DatabaseSchema.Games.COLUMN_NAME_RANK)
                val typeColumn = cursor.getColumnIndex(DatabaseSchema.Games.COLUMN_NAME_TYPE)
                val imageColumn = cursor.getColumnIndex(DatabaseSchema.Games.COLUMN_NAME_IMAGE)
                val yearColumn = cursor.getColumnIndex(DatabaseSchema.Games.COLUMN_NAME_YEAR)

                while (moveToNext()) {
                    val id = getLong(idColumn)
                    val title = getStringOrNull(titleColumn)
                    val rank = getIntOrNull(rankColumn)
                    val type = getString(typeColumn)
                    val image = getStringOrNull(imageColumn)
                    val year = getIntOrNull(yearColumn)
                    games.add(Game(id, title, year, rank, image, Type.valueOf(type)))
                }
            }

            cursor.close()
            return games
        }
    }

}