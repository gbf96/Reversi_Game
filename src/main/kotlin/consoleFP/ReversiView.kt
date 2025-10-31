package consoleFP

import model.BOARD_SIDE
import model.Coordinate
import model.GameState

import model.PiecesColor.BLACK
import model.PiecesColor.WHITE
import model.Reversi
import model.countPieces
import model.validTargets

/**
 * Displays the current state of the Reversi board.
 * Shows the position of the pieces, and the current player's turn.
 *  Optionally highlights valid targets for playing.
 *
 * @param game The current instance of the Reversi game to display.
 * @param showTargets A boolean indicating whether to highlight valid target positions.
 */
fun display(context: CommandContext) {
    if (context is CommandContext.DistributedGame) println("You are player ${context.clash.sidePlayer} in game ${context.clash.name}")
    if (context !is CommandContext.WithGame) throw IllegalStateException("Game not started")
    val game = context.reversi
    val targets = game.validTargets
    val letters = buildString {
        append("  ")
        for (c in 0 until BOARD_SIDE) {
            append(" ${('A'.code + c).toChar()} ")
        }
    }
    println(letters)

    for (row in 0 until BOARD_SIDE) {
        val line = buildString {
            append(String.format("%2d", row + 1))
            for (column in 0 until BOARD_SIDE) {
                val coordinate = Coordinate(row, column)
                var c = when(val pieceColor = game[coordinate]){
                    BLACK -> pieceColor.symbol
                    WHITE -> pieceColor.symbol
                    null -> '.'
                }
                if (context.showTargets && coordinate in targets) c = '*'
                append(" $c ")
            }
        }
        println(line)
    }
    when(game.gameState){
        is GameState.Run, is GameState.Pass -> {
            println("${BLACK.symbol} = ${game.countPieces(BLACK)} | ${WHITE.symbol} = ${game.countPieces(WHITE)}")
            println("Turn: " + game.currentPlayer.toString())
        }
        is GameState.Draw -> println("Draw")
        is GameState.Win -> println("Winner: ${game.gameState.winner}")
    }
}