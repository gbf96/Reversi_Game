package storage

import model.Coordinate
import model.PiecesColor
import model.Reversi
import model.GameState

/**
 * Serializer for the [Reversi] game state.
 * This object is responsible for converting a [Reversi] game instance into a string representation
 * and vice-versa.
 *
 * The serialization format consists of two lines:
 * 1.  **Game State Line**: Describes the current state of the game.
 *     - `Run:<player>`: The game is in progress, and it's `<player>`'s turn.
 *     - `Win:<player>`: The game has ended, and `<player>` is the winner.
 *     - `Pass:<player>`: The current player had to pass their turn, and it's now `<player>`'s turn.
 *     - `Draw`: The game ended in a draw.
 *     The `<player>` token is `0` for BLACK and `1` for WHITE.
 *
 * 2.  **Pieces Line**: A space-separated list of pieces on the board.
 *     - Each piece is represented as `<coord>:<color>`.
 *     - `<coord>` is the coordinate in the format `RowLetter` (e.g., `4D`). Rows are 1-based.
 *     - `<color>` is the piece color, `0` for BLACK and `1` for WHITE.
 */
object GameSerializer : Serializer<Reversi> {

    private fun colorToToken(color: PiecesColor): String = if (color == PiecesColor.BLACK) "0" else "1"

    private fun tokenToColor(token: String): PiecesColor? = when (token) {
        "0" -> PiecesColor.BLACK
        "1" -> PiecesColor.WHITE
        else -> null
    }

    private fun coordinateToToken(coord: Coordinate): String {
        return "${coord.row + 1}${('A' + coord.column)}"
    }

    /**
     * Parses a string token into a [Coordinate].
     * The expected format is a 1-based row number followed by a single column letter (e.g., "4D", "12A").
     *
     * @param token The string to parse.
     * @return The corresponding [Coordinate], or `null` if the token format is invalid.
     */
    private fun tokenToCoordinate(token: String): Coordinate? {
        val rowStr = token.takeWhile { it.isDigit() }
        val colStr = token.substring(rowStr.length)
        if (rowStr.isEmpty() || colStr.length != 1) return null
        val colChar = colStr.first()
        //if (!colChar.isLetter()) return null
        return Coordinate(rowStr.toInt() - 1, colChar - 'A')
    }

    /**
     * Serializes a [Reversi] game object into its string representation.
     *
     * The output string follows the two-line format defined in this serializer:
     * 1. A line for the game state (e.g., "Run:0", "Win:1", "Draw").
     * 2. A line for the pieces, with each piece as "Coord:Color" separated by spaces.
     *
     * @param game The [Reversi] game instance to be serialized.
     * @return A string containing the serialized game data, ready to be stored or transmitted.
     */
    override fun serialize(game: Reversi): String {
        val playerToken = colorToToken(game.currentPlayer)
        val stateLine = when (game.gameState) {
            is GameState.Draw -> "Draw"
            is GameState.Run -> "Run:$playerToken"
            is GameState.Win -> "Win:$playerToken"
            is GameState.Pass -> "Pass:${playerToken}"
        }

        val piecesLine = game.pieces.entries.joinToString(" ") { (coord, color) ->
            "${coordinateToToken(coord)}:${colorToToken(color)}"
        }

        return "$stateLine\n$piecesLine"

    }

    /**
     * Deserializes a string representation of a Reversi game into a [Reversi] object.
     *
     * This function parses a string that was previously created by the [serialize] method,
     * reconstructing the game state, current player, and board pieces.
     *
     * @param txt The string containing the serialized game data. It must follow the format
     *            defined in this serializer (two lines: state and pieces).
     * @return A new [Reversi] instance representing the game state from the input string.
     * @throws IllegalArgumentException if the input string `txt` has an invalid format,
     *         contains an unknown game state, or has malformed piece/color tokens.
     */
    override fun deserialize(txt: String): Reversi {
        val lines = txt.lines()
        require(lines.size == 2) { "Invalid format, must have exactly two lines" }

        val stateParts = lines[0].split(':')
        val stateType = stateParts[0]

        val gameState: GameState
        val currentPlayer: PiecesColor

        when (stateType) {
            "Run", "Win", "Pass" -> {
                val playerToken = stateParts.getOrNull(1)
                require(playerToken != null) { "Missing player token for game state: $stateType" }
                currentPlayer = requireNotNull(tokenToColor(playerToken)) { "Invalid player token: $playerToken" }
                gameState = when (stateType) {
                    "Run" -> GameState.Run
                    "Win" -> GameState.Win(currentPlayer)
                    else -> GameState.Pass(currentPlayer) // "Pass"
                }
            }
            "Draw" -> {
                require(stateParts.size == 1) { "Draw state should not have a player token" }
                gameState = GameState.Draw
                currentPlayer = PiecesColor.BLACK // Default player for draw state, as Reversi needs one
            }
            else -> throw IllegalArgumentException("Unknown game state: $stateType")
        }

        val pieces = if (lines[1].isBlank()) {
            emptyMap()
        } else {
            lines[1].split(' ').associate { pieceToken ->
                val (coordToken, colorToken) = pieceToken.split(':')
                val coord = requireNotNull(tokenToCoordinate(coordToken)) { "Invalid coordinate token: $coordToken" }
                val color = requireNotNull(tokenToColor(colorToken)) { "Invalid color token: $colorToken" }
                coord to color
            }
        }

        return Reversi(currentPlayer, gameState, pieces)
    }
}