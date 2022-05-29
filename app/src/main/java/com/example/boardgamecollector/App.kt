package com.example.boardgamecollector

import java.text.SimpleDateFormat
import java.util.*

object App {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH)
    const val collectionUrl = "https://boardgamegeek.com/xmlapi2/collection?username=%s&stats=1"
    const val expansionCollectionUrl =
        "https://boardgamegeek.com/xmlapi2/collection?username=%s&subtype=boardgameexpansion"
    const val INTENT_EXTRA_ID = "id"
    const val THUMBNAIL_QUALITY = 95
    const val DEFAULT_DATE = "01.01.1970 00:00:00"
}