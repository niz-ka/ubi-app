package com.example.boardgamecollector

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class XmlParser(inputStream: InputStream) {

    private val ns: String? = null
    private val parser: XmlPullParser = Xml.newPullParser()

    init {
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)
    }

    companion object {
        private const val TAG = "XmlParser"
    }

    private inner class GameTextModel(
        val id: String,
        var title: String?,
        var year: String?,
        var rank: String?,
        var image: String?,
    )

    fun parseUserCollection(): List<Game> {
        parser.nextTag()
        parser.require(XmlPullParser.START_TAG, ns, "items")

        val gamesText = mutableListOf<GameTextModel>()

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (
                parser.eventType == XmlPullParser.START_TAG
                && parser.name == "item"
                && parser.getAttributeValue(ns, "subtype") in arrayOf(
                    "boardgame",
                    "boardgameexpansion"
                )
            ) {
                val game = parseGame()
                gamesText.add(game)
            }
        }

        val distinct = gamesText.distinctBy { it.id }
        return convert(distinct)
    }

    private fun convert(gamesText: List<GameTextModel>): List<Game> {
        val games = mutableListOf<Game>()

        for (game in gamesText) {
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

            games.add(Game(id, title, year, rank, image, Game.Type.BOARD_GAME))
        }

        return games
    }

    private fun parseGame(): GameTextModel {
        parser.require(XmlPullParser.START_TAG, ns, "item")

        val id = parser.getAttributeValue(ns, "objectid")
        val gameText = GameTextModel(id, null, null, null, null)

        while (parser.next() != XmlPullParser.END_TAG || parser.name != "item") {
            // Name -> title
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == "name") {
                parser.require(XmlPullParser.START_TAG, ns, "name")
                while (true)
                    if (parser.next() == XmlPullParser.TEXT) break
                gameText.title = parser.text
            }

            // yearpublished -> year
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == "yearpublished") {
                parser.require(XmlPullParser.START_TAG, ns, "yearpublished")
                while (true)
                    if (parser.next() == XmlPullParser.TEXT) break
                gameText.year = parser.text
            }

            // thumbnail -> image
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == "thumbnail") {
                parser.require(XmlPullParser.START_TAG, ns, "thumbnail")
                while (true)
                    if (parser.next() == XmlPullParser.TEXT) break
                gameText.image = parser.text
            }

            // rank
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == "rank" && parser.getAttributeValue(
                    ns,
                    "name"
                ) == "boardgame"
            ) {
                parser.require(XmlPullParser.START_TAG, ns, "rank")
                val value = parser.getAttributeValue(ns, "value")
                if (value != null && value.lowercase() != "not ranked")
                    gameText.rank = value
            }

        }
        parser.require(XmlPullParser.END_TAG, ns, "item")
        return gameText
    }

    fun getExpansionIds(): List<Long> {
        parser.nextTag()
        parser.require(XmlPullParser.START_TAG, ns, "items")

        val expansionIds = mutableListOf<Long>()

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG
                && parser.name == "item"
                && parser.getAttributeValue(ns, "subtype") == "boardgameexpansion"
            ) {
                val id = parser.getAttributeValue(ns, "objectid").toLong()
                expansionIds.add(id)
            }
        }

        return expansionIds.distinctBy { it }
    }
}