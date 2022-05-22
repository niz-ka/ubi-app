package com.example.boardgamecollector

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

class XmlParser {
    companion object {
        private val ns: String? = null

        private fun setParser(inputStream: InputStream): XmlPullParser {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            return parser
        }

        fun countTags(inputStream: InputStream): Int {
            val parser = setParser(inputStream)
            var count = 0
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG)
                    ++count
            }
            return count
        }

        fun parseUserCollection(inputStream: InputStream): List<Game> {
            val parser = setParser(inputStream)
            parser.nextTag()
            parser.require(XmlPullParser.START_TAG, ns, "items")

            val gamesCount = parser.getAttributeValue(ns, "totalitems")
            val games = mutableListOf<Game>()

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
               if(
                   parser.eventType == XmlPullParser.START_TAG
                   && parser.name == "item"
                   && parser.getAttributeValue(ns, "subtype") == "boardgame"
               ) {
                   val game = parseGame(parser)
                   games.add(game)
               }
            }

            return games
        }

        private fun parseGame(parser: XmlPullParser): Game {
            parser.require(XmlPullParser.START_TAG, ns, "item")

            val id: Long = parser.getAttributeValue(ns, "objectid").toLong()
            var title: String? = null
            var year: Int? = null
            var rank: Int? = null
            var image: String? = null
            val type: Game.Type = Game.Type.BOARD_GAME

            while(parser.next() != XmlPullParser.END_TAG || parser.name != "item") {
                // Name -> title
                if(parser.eventType == XmlPullParser.START_TAG && parser.name == "name") {
                    parser.require(XmlPullParser.START_TAG, ns, "name")
                    while(true)
                        if(parser.next() == XmlPullParser.TEXT) break
                    title = parser.text
                }

                // yearpublished -> year
                if(parser.eventType == XmlPullParser.START_TAG && parser.name == "yearpublished") {
                    parser.require(XmlPullParser.START_TAG, ns, "yearpublished")
                    while(true)
                        if(parser.next() == XmlPullParser.TEXT) break
                    year = parser.text.toInt()
                }

                // thumbnail -> image
                if(parser.eventType == XmlPullParser.START_TAG && parser.name == "thumbnail") {
                    parser.require(XmlPullParser.START_TAG, ns, "thumbnail")
                    while(true)
                        if(parser.next() == XmlPullParser.TEXT) break
                    image = parser.text
                }

                // rank
                if(parser.eventType == XmlPullParser.START_TAG && parser.name == "rank" && parser.getAttributeValue(ns, "name") == "boardgame") {
                    parser.require(XmlPullParser.START_TAG, ns, "rank")
                    val value = parser.getAttributeValue(ns, "value")
                    if(value != null && value.lowercase() != "not ranked")
                        rank = value.toInt()
                }

            }
            parser.require(XmlPullParser.END_TAG, ns, "item")
            return Game(id, title, year, rank, image, type)
        }
    }
}