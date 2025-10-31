package console

import model.Coordinate
import model.Game

sealed interface CommandContext {
    object Empty : CommandContext

    data class GameInProgress(
        val game: Game,
        val showTargets: Boolean
    ) : CommandContext {

        fun play(coordinate: Coordinate): GameInProgress? {
            val newGame = game.play(coordinate) ?: return null
            return this.copy(game = newGame)
        }

        fun pass(): GameInProgress? {
            val newGame = game.pass() ?: return null
            return this.copy(game = newGame)
        }

        fun show() = display(game.reversi, showTargets)
    }
}
