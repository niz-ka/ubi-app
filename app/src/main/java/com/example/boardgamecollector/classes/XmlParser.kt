package com.example.boardgamecollector.classes

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.Xml
import com.example.boardgamecollector.models.Game
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class XmlParser(inputStream: InputStream) {

    private val ns: String? = null
    private val parser: XmlPullParser = Xml.newPullParser()
    private lateinit var textGamesCollection: List<GameTextModel>

    private var counter: Int = 0
    private val userCollection = mutableListOf<Game>()

    init {
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)
    }

    companion object {
        private const val TAG = "XmlParser"
        private const val TOKEN_ITEMS = "items"
        private const val TOKEN_ITEM = "item"
        private const val TOKEN_SUBTYPE = "subtype"
        private const val TOKEN_BOARD_GAME = "boardgame"
        private const val TOKEN_BOARD_GAME_EXPANSION = "boardgameexpansion"
        private const val TOKEN_NAME = "name"
        private const val TOKEN_OBJECT_ID = "objectid"
        private const val TOKEN_YEAR_PUBLISHED = "yearpublished"
        private const val TOKEN_THUMBNAIL = "thumbnail"
        private const val TOKEN_NOT_RANKED = "not ranked"
        private const val TOKEN_RANK = "rank"
        private const val TOKEN_VALUE = "value"
    }

    private inner class GameTextModel(
        val id: String,
        var title: String?,
        var year: String?,
        var rank: String?,
        var image: String?,
    )

    fun parseUserCollection(): Int {
        parser.nextTag()
        parser.require(XmlPullParser.START_TAG, ns, TOKEN_ITEMS)

        val gamesText = mutableListOf<GameTextModel>()

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (
                parser.eventType == XmlPullParser.START_TAG
                && parser.name == TOKEN_ITEM
                && parser.getAttributeValue(ns, TOKEN_SUBTYPE) in arrayOf(
                    TOKEN_BOARD_GAME,
                    TOKEN_BOARD_GAME_EXPANSION
                )
            ) {
                val game = parseGame()
                gamesText.add(game)
            }
        }

        textGamesCollection = gamesText.distinctBy { it.id }
        return textGamesCollection.size
    }

    fun nextConversion() {
        val game = textGamesCollection[counter]

        val id = game.id.toLong()
        val title = game.title
        val year = game.year?.toInt()
        val rank = game.rank?.toInt()

        var image: Bitmap? = null
        if (game.image != null) {
            try {
                val url = URL(game.image)
                val connection = url.openConnection() as HttpURLConnection

                connection.inputStream.use {
                    image = BitmapFactory.decodeStream(it)
                }
            } catch (exception: Exception) {
                Log.e(TAG, exception.toString())
            }
        }

        userCollection.add(Game(id, title, year, rank, image, Game.Type.BOARD_GAME))
        ++counter
    }

    fun getUserCollection(): List<Game> {
        return userCollection
    }

    private fun parseGame(): GameTextModel {
        parser.require(XmlPullParser.START_TAG, ns, TOKEN_ITEM)

        val id = parser.getAttributeValue(ns, TOKEN_OBJECT_ID)
        val gameText = GameTextModel(id, null, null, null, null)

        while (parser.next() != XmlPullParser.END_TAG || parser.name != TOKEN_ITEM) {

            if (parser.eventType == XmlPullParser.START_TAG && parser.name == TOKEN_NAME) {
                parser.require(XmlPullParser.START_TAG, ns, TOKEN_NAME)
                while (true)
                    if (parser.next() == XmlPullParser.TEXT) break
                gameText.title = parser.text
            }

            if (parser.eventType == XmlPullParser.START_TAG && parser.name == TOKEN_YEAR_PUBLISHED) {
                parser.require(XmlPullParser.START_TAG, ns, TOKEN_YEAR_PUBLISHED)
                while (true)
                    if (parser.next() == XmlPullParser.TEXT) break
                gameText.year = parser.text
            }

            if (parser.eventType == XmlPullParser.START_TAG && parser.name == TOKEN_THUMBNAIL) {
                parser.require(XmlPullParser.START_TAG, ns, TOKEN_THUMBNAIL)
                while (true)
                    if (parser.next() == XmlPullParser.TEXT) break
                gameText.image = parser.text
            }

            if (parser.eventType == XmlPullParser.START_TAG && parser.name == TOKEN_RANK && parser.getAttributeValue(
                    ns,
                    TOKEN_NAME
                ) == TOKEN_BOARD_GAME
            ) {
                parser.require(XmlPullParser.START_TAG, ns, TOKEN_RANK)
                val value = parser.getAttributeValue(ns, TOKEN_VALUE)
                if (value != null && value.lowercase() != TOKEN_NOT_RANKED)
                    gameText.rank = value
            }

        }
        parser.require(XmlPullParser.END_TAG, ns, TOKEN_ITEM)
        return gameText
    }

    fun getExpansionIds(): List<Long> {
        parser.nextTag()
        parser.require(XmlPullParser.START_TAG, ns, TOKEN_ITEMS)

        val expansionIds = mutableListOf<Long>()

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG
                && parser.name == TOKEN_ITEM
                && parser.getAttributeValue(ns, TOKEN_SUBTYPE) == TOKEN_BOARD_GAME_EXPANSION
            ) {
                val id = parser.getAttributeValue(ns, TOKEN_OBJECT_ID).toLong()
                expansionIds.add(id)
            }
        }

        return expansionIds.distinctBy { it }
    }
}