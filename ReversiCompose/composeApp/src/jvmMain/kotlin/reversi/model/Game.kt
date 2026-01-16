package model

/**
 * Represents a generic game interface for the Reversi game logic.
 * It defines the contract for playing and passing turns, as well as
 * accessing the current Reversi game state.
 */
interface Game {
    /**
     * Executes a move on the game board at the specified coordinate.
     *
     * @param coordinate The coordinate on the game board where the move is attempted.
     * @return A new instance of the game with the updated state after the move,
     *         or null if the move is invalid.
     * @throws IllegalStateException If the game is already over.
     */
    fun play(coordinate: Coordinate): Game?

    /**
     * Passes the current turn to the opponent in the game, updating the game state.
     *
     * @return A new instance of the game with the updated state after passing the turn,
     *         or null if passing the turn is invalid.
     * @throws IllegalStateException If the game is already over.
     */
    fun pass(): Game?

    /**
     * Represents the current state of the Reversi game.
     * This property provides access to an instance of the `Reversi` class, which encapsulates
     * the state of the game, including the board, the current player, and game-specific logic.
     */
    val reversi: Reversi
}