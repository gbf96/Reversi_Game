package model

/**
 * Represents the state of a Reversi game.
 */
sealed class GameState{
    /**
     * Represents a game-ending state in which one of the players has won the game.
     *
     * @property winner The color of the player pieces that corresponds to the winner.
     */
    data class Win(val winner: PiecesColor): GameState()

    /**
     * Represents the state of a game where the match has ended in a draw.
     */
    data object Draw: GameState()

    /**
     * Represents the state of the Reversi game during an ongoing match.
     */
    data object Run: GameState()

    /**
     * Represents a game state in Reversi where the current player passes their turn without making a move.
     *
     * @property passer The color of the player passing their turn.
     */
    data class Pass(val passer: PiecesColor): GameState()
}


