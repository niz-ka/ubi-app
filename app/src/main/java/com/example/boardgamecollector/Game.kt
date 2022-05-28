package com.example.boardgamecollector

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.database.getBlobOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import java.io.ByteArrayOutputStream

class Game(
    val id: Long,
    val title: String?,
    val year: Int?,
    val rank: Int?,
    val image: Bitmap?,
    var type: Type
) {

    enum class Type {
        BOARD_GAME,
        BOARD_EXPANSION
    }

    override fun toString(): String {
        return "Game(id=$id, title=$title, year=$year, rank=$rank, image=$image, type=$type)"
    }

    companion object {
        fun insertOne(game: Game): Long {

            var image: ByteArray? = null

            if (game.image != null) {
                val imageStream = ByteArrayOutputStream()
                game.image.compress(Bitmap.CompressFormat.JPEG, 95, imageStream)
                image = imageStream.toByteArray()
            }

            val db = DatabaseHelper.db.writableDatabase
            val values = ContentValues()
            values.put(DatabaseSchema.Games.COLUMN_NAME_ID, game.id)
            values.put(DatabaseSchema.Games.COLUMN_NAME_TITLE, game.title)
            values.put(DatabaseSchema.Games.COLUMN_NAME_YEAR, game.year)
            values.put(DatabaseSchema.Games.COLUMN_NAME_RANK, game.rank)
            values.put(DatabaseSchema.Games.COLUMN_NAME_IMAGE, image)
            values.put(DatabaseSchema.Games.COLUMN_NAME_TYPE, game.type.toString())
            return db.insertWithOnConflict(DatabaseSchema.Games.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
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

        fun findOne(gameId: Long): Game? {
            val db = DatabaseHelper.db.readableDatabase

            val projection = arrayOf(
                DatabaseSchema.Games.COLUMN_NAME_ID,
                DatabaseSchema.Games.COLUMN_NAME_TITLE,
                DatabaseSchema.Games.COLUMN_NAME_RANK,
                DatabaseSchema.Games.COLUMN_NAME_TYPE,
                DatabaseSchema.Games.COLUMN_NAME_IMAGE,
                DatabaseSchema.Games.COLUMN_NAME_YEAR
            )

            val selection = "${DatabaseSchema.Games.COLUMN_NAME_ID} = ?"
            val selectionArgs = arrayOf(gameId.toString())

            val cursor = db.query(
                DatabaseSchema.Games.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
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

                if (!moveToFirst()) {
                    close()
                    return null
                }

                val id = getLong(idColumn)
                val title = getStringOrNull(titleColumn)
                val rank = getIntOrNull(rankColumn)
                val type = getString(typeColumn)

                val imageBytes = getBlobOrNull(imageColumn)
                var image: Bitmap? = null

                if (imageBytes != null)
                    image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                val year = getIntOrNull(yearColumn)

                close()
                return Game(id, title, year, rank, image, Type.valueOf(type))
            }
        }

        fun findAll(gameType: Type): List<Game> {
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

            val selection = "${DatabaseSchema.Games.COLUMN_NAME_TYPE} = ?"
            val selectionArgs = arrayOf(gameType.toString())

            val cursor = db.query(
                DatabaseSchema.Games.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
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

                    val imageBytes = getBlobOrNull(imageColumn)
                    var image: Bitmap? = null

                    if (imageBytes != null)
                        image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    val year = getIntOrNull(yearColumn)
                    games.add(Game(id, title, year, rank, image, Type.valueOf(type)))
                }

                close()
            }

            return games
        }
    }

}