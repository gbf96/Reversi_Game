package model

import storage.Storage
import java.lang.IllegalArgumentException

typealias GameStorage = Storage<Name, Reversi>

/**
 * Represents a Reversi game session, storing the game's state and player information.
 *
 * @property storage The game storage system used to persist the game state.
 * @property name The name of the current game.
 * @property sidePlayer The color of the pieces controlled by the local player.
 * @property reversi The current state of the Reversi game.
 * @property showTargets A boolean flag indicating whether available moves for the current player should be shown.
 */
data class Clash(
    val storage: GameStorage,
    val name: Name,
    val sidePlayer: PiecesColor,
    override val reversi: Reversi,
    val showTargets: Boolean): Game{

    /**
     * Executes a move in the game by attempting to place a piece at the specified coordinate.
     *
     * @param coordinate The coordinate on the game board where the move is attempted.
     * @return A new instance of `Clash` with the updated game state, or null if the move is invalid.
     * @throws IllegalStateException If it is not the player's turn or the game is already over.
     */
    override fun play(coordinate: Coordinate): Clash? {
        if (!isMyTurn() && !reversi.isGameOver())
            throw IllegalStateException("It's not your turn!")


        val newReversi = reversi.play(coordinate) ?: return null

        storage.update(name, newReversi)
        return this.copy(reversi = newReversi)
    }

    /**
     * Passes the current turn to the opponent in the game, updating the game state.
     *
     * @return A new instance of `Clash` with the updated game state after passing the turn,
     *         or null if passing the turn is not valid.
     * @throws IllegalStateException If it is not the player's turn or the game is already over.
     */
    override fun pass(): Clash? {
        if (!isMyTurn() && !reversi.isGameOver())
            throw IllegalStateException("It's not your turn!")

        val newReversi = reversi.pass()
            ?: return null

        storage.update(name, newReversi)
        return this.copy(reversi = newReversi)
    }

    companion object{
        /**
         * Starts a new Clash game with the specified parameters and persists its initial state.
         *
         * @param name The name of the game.
         * @param st The game storage used to persist the game's state.
         * @param color The color of the starting player's pieces.
         * @return A new instance of a Clash representing the initialized game.
         * @Throws IllegalArgumentException if a game with the same name already exists in the storage.
         */
        fun start(name: Name, st: GameStorage, color: PiecesColor): Clash {
            if (st.read(name) != null) {
                throw IllegalArgumentException("File '$name' already exists.")
            }

            return Clash(st, name, color, Reversi(color), false).also {
                st.create(name, it.reversi)
            }
        }

        /**
         * Joins an existing game identified by its name, loading its current state
         * and determining the appropriate side for the player attempting to join.
         *
         * @param name The unique identifier of the game to join.
         * @param st The storage system used to retrieve the game state.
         * @return A `Clash` instance representing the joined game and its updated state.
         * @throws IllegalArgumentException If the game does not exist in the storage.
         * @throws IllegalStateException If the game is not in a state that allows joining.
         */
        fun join(name: Name, st: GameStorage): Clash {
            val loadedReversi = st.read(name)
                ?: throw IllegalArgumentException("Game $name does not exist")
            val sidePlayer = when(loadedReversi.countPieces()){
                4 -> loadedReversi.currentPlayer.other()
                5 -> loadedReversi.currentPlayer
                else -> throw IllegalStateException("Can´t join in game $name")
            }
            return Clash(st, name, sidePlayer, loadedReversi, false)
        }
    }
}

/**
 * Determines whether it is currently the turn of the player associated with this `Clash` instance.
 * @return `true` if it is the turn of the player represented by this `Clash` instance,
 *         `false` otherwise.
 */
fun Clash.isMyTurn() = reversi.currentPlayer == this.sidePlayer

/**
 * Refreshes the current state of the game by reloading its data from storage.
 *
 * @return A new instance of the `Clash` class with the updated game state.
 * @throws IllegalStateException If the game is already over.
 * @throws IllegalArgumentException  if the game data cannot be found.
 */
fun Clash.refresh(): Clash{
    if (reversi.isGameOver())
        throw IllegalStateException("Game ${name.value} is over")

    return this.copy(reversi = storage.read(name) ?: throw IllegalArgumentException("Game file ${name.value} not found!"))
}


