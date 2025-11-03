package console

import model.Clash
import model.Coordinate
import model.Game
import model.isMyTurn

/**
 * Represents the context in which a command is executed.
 */
sealed interface CommandContext {
    /**
     * Represents an empty command context.
     */
    object Empty : CommandContext

    /**
     * Represents an ongoing game state within a command context.
     *
     * @property game The current game being played.
     * @property showTargets A boolean flag that determines whether valid targets should be highlighted during display.
     */
    data class GameInProgress(
        val game: Game,
        val showTargets: Boolean
    ) : CommandContext {

        /**
         * Plays a move at the specified coordinate within the ongoing game.
         *
         * @param coordinate The coordinate where the move should be played.
         * @return A new `GameInProgress` instance with the updated game state if the move is valid,
         *         or null if the move is invalid.
         * @throws IllegalStateException If the game is already over.
         */
        fun play(coordinate: Coordinate): GameInProgress? {
            val newGame = game.play(coordinate) ?: return null
            return this.copy(game = newGame)
        }

        /**
         * Passes the current turn to the opponent by updating the game's state.
         *
         * @return A new instance of `GameInProgress` with the updated game state after passing the turn,
         *         or null if passing the turn is invalid.
         * @throws IllegalStateException If the game is already over.
         */
        fun pass(): GameInProgress? {
            val newGame = game.pass() ?: return null
            return this.copy(game = newGame)
        }

        /**
         * Displays the current state of the ongoing game.
         */
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
