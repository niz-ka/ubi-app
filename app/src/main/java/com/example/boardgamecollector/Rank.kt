package com.example.boardgamecollector

import android.content.ContentValues
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import java.util.*

class Rank(
    val id: Long,
    val date: Date?,
    val rank: Int?
) {

    companion object {

        fun insertOne(rank: Rank): Long {
            val db = DatabaseHelper.db.writableDatabase
            val values = ContentValues()
            values.put(DatabaseSchema.Ranks.COLUMN_NAME_ID, rank.id)
            values.put(
                DatabaseSchema.Ranks.COLUMN_NAME_DATE,
                App.formatter.format(rank.date ?: Date())
            )
            values.put(DatabaseSchema.Ranks.COLUMN_NAME_RANK, rank.rank)
            return db.insert(DatabaseSchema.Ranks.TABLE_NAME, null, values)
        }

        fun insertMany(ranks: List<Rank>): List<Long> {
            val ids = mutableListOf<Long>()
            for (rank in ranks)
                ids.add(insertOne(rank))
            return ids
        }

        fun findAllById(gameId: Long): List<Rank> {
            val db = DatabaseHelper.db.readableDatabase
            val ranks = mutableListOf<Rank>()

            val projection = arrayOf(
                DatabaseSchema.Ranks.COLUMN_NAME_ID,
                DatabaseSchema.Ranks.COLUMN_NAME_DATE,
                DatabaseSchema.Ranks.COLUMN_NAME_RANK,
            )

            val selection = "${DatabaseSchema.Ranks.COLUMN_NAME_ID} = ?"
            val selectionArgs = arrayOf(gameId.toString())

            val cursor = db.query(
                DatabaseSchema.Ranks.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            with(cursor) {
                val idColumn = cursor.getColumnIndex(DatabaseSchema.Ranks.COLUMN_NAME_ID)
                val dateColumn = cursor.getColumnIndex(DatabaseSchema.Ranks.COLUMN_NAME_DATE)
                val rankColumn = cursor.getColumnIndex(DatabaseSchema.Ranks.COLUMN_NAME_RANK)

                while (moveToNext()) {
                    val id = getLong(idColumn)
                    val date = getStringOrNull(dateColumn)
                    val rank = getIntOrNull(rankColumn)

                    ranks.add(Rank(id, App.formatter.parse(date ?: App.DEFAULT_DATE), rank))
                }

                close()
            }

            return ranks
        }

    }

    override fun toString(): String {
        return "Rank(id=$id, date=$date, rank=$rank)"
    }

}