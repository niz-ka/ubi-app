package com.example.boardgamecollector

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


}