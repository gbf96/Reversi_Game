package storage

import model.Coordinate
import model.PiecesColor
import model.Reversi
import model.GameState

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

    private fun tokenToCoordinate(token: String): Coordinate? {
        val rowStr = token.takeWhile { it.isDigit() }
        val colChar = token.lastOrNull()
        if (rowStr.isEmpty() || colChar == null || !colChar.isLetter()) return null
        return Coordinate(rowStr.toInt() - 1, colChar - 'A')
    }

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

    override fun deserialize(txt: String): Reversi {
        val lines = txt.lines()
        require(lines.size >= 2) { "Invalid format, must have at least two lines" }

        val stateParts = lines[0].split(':')
        val stateType = stateParts[0]
        val playerToken = stateParts.getOrNull(1)

        val currentPlayer = playerToken?.let { tokenToColor(it) } ?: PiecesColor.BLACK

        val gameState = when (stateType) {
            "Run" -> GameState.Run
            "Win" -> GameState.Win(currentPlayer)
            "Pass" -> GameState.Pass(currentPlayer)
            "Draw" -> GameState.Draw
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