package com.example.boardgamecollector

import android.content.ContentValues

class Setting(
    val key: String,
    val value: String?,
) {

    companion object {
        fun findOne(key: String): Setting? {
            val db = DatabaseHelper.db.readableDatabase
            val projection = arrayOf(
                DatabaseSchema.Settings.COLUMN_NAME_KEY,
                DatabaseSchema.Settings.COLUMN_NAME_VALUE
            )
            val selection = "${DatabaseSchema.Settings.COLUMN_NAME_KEY} = ?"
            val selectionArgs = arrayOf(key)

            val cursor = db.query(
                DatabaseSchema.Settings.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            if (!cursor.moveToFirst()) {
                cursor.close()
                return null
            }

            val keyColumn = cursor.getColumnIndex(DatabaseSchema.Settings.COLUMN_NAME_KEY)
            val valueColumn = cursor.getColumnIndex(DatabaseSchema.Settings.COLUMN_NAME_VALUE)

            val settingKey = cursor.getString(keyColumn)
            val settingValue = cursor.getString(valueColumn)
            val setting = Setting(settingKey, settingValue)

            cursor.close()
            return setting
        }

        fun deleteOne(key: String): Int {
            val db = DatabaseHelper.db.writableDatabase
            return db.delete(
                DatabaseSchema.Settings.TABLE_NAME,
                "${DatabaseSchema.Settings.COLUMN_NAME_KEY} = ?",
                arrayOf(key)
            )
        }

        fun insertOrUpdateOne(setting: Setting): Long {
            deleteOne(setting.key)
            val db = DatabaseHelper.db.writableDatabase
            val values = ContentValues()
            values.put(DatabaseSchema.Settings.COLUMN_NAME_KEY, setting.key)
            values.put(DatabaseSchema.Settings.COLUMN_NAME_VALUE, setting.value)
            return db.insert(DatabaseSchema.Settings.TABLE_NAME, null, values)
        }
    }

    override fun toString(): String {
        return "Setting(key='$key', value='$value')"
    }

}