package model

interface Game {
    fun play(coordinate: Coordinate): Game?

    fun pass(): Game?

    val reversi: Reversi
}