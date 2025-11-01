package console

import model.Clash
import model.Coordinate
import model.Game
import model.isMyTurn

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

        fun show() {
            if (game is Clash){
                println("You are player ${game.sidePlayer} in game ${game.name}")
                display(game.reversi, showTargets && game.isMyTurn())
            }else {
                display(game.reversi, showTargets)
            }
        }
    }
}
