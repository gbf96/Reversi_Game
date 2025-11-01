package model

const val BOARD_SIDE = 8
const val BOARD_SIZE = BOARD_SIDE * BOARD_SIDE

private const val MIN_SIDE = 4
private const val MAX_SIDE = 26

typealias Board = Map<Coordinate, PiecesColor>
/**
 * Represents the state of a Reversi game.
 * The game state includes the current player and the placement of pieces
 * on the board. This class is immutable; all state updates produce a new instance.
 *
 * @property currentPlayer The player taking the current turn.
 * @property pieces A map representing the Reversi board. The keys are coordinates,
 *                  and the values are the pieces' colors at those coordinates.
 */
data class Reversi(
    val currentPlayer: PiecesColor,
    val gameState: GameState,
    val pieces: Board
): Game{
    override val reversi
        get() = this

    val validTargets: Set<Coordinate>

    init {
        validateBoardSide()
        validTargets = validTargets()
    }
    /**
     * Creates a new Reversi game with the given player as the first player.
     * @param playerOne The player to start the game.
     */
    constructor(playerOne: PiecesColor) : this(
        currentPlayer = playerOne,
        gameState = GameState.Run,
        pieces = buildMap {
            val mid = BOARD_SIDE / 2 - 1
            put(Coordinate(mid, mid), PiecesColor.WHITE)
            put(Coordinate(mid+1, mid+1), PiecesColor.WHITE)
            put(Coordinate(mid+1, mid), PiecesColor.BLACK)
            put(Coordinate(mid, mid+1), PiecesColor.BLACK)
        }
    )

    /**
     * Returns the piece color at the given coordinate, or null if the coordinate is empty.
     * @param at The coordinate of the piece to return.
     * @return The piece color or null, if [at] is empty.
     */
    operator fun get(at: Coordinate) = pieces[at]

    /**
     * Changes the color of a piece on the board at the given coordinate.
     * If a piece is present at the given coordinate, its color will be replaced by its opposite.
     *
     * @param coordinate The coordinate of the piece whose color is to be changed.
     * @return A new instance of the Reversi game with the updated state of the board.
     * @throws IllegalArgumentException If there is no piece at the given coordinate.
     */
    fun changePieceColor(coordinate: Coordinate): Reversi{
        val currentColor = pieces[coordinate]
        require(currentColor != null) { "No piece at coordinate $coordinate" }
        return this.copy(
            pieces = pieces + (coordinate to currentColor.other())
        )
    }

    fun Reversi.changePiecesColorTo(coords: Set<Coordinate>, color: PiecesColor): Reversi {
        val flipped: Map<Coordinate, PiecesColor> = coords.associateWith { color }//------------------------------
        val newPieces = pieces + flipped
        return copy(pieces = newPieces)
    }

    /**
     * Performs a move in the Reversi game by placing a piece at the specified coordinate.
     * This method ensures that the move is valid, flips the opponent's pieces accordingly,
     * and updates the game state.
     *
     * @param coordinate The coordinate where the piece is to be placed.
     * @return A new instance of the Reversi game with the updated board and game state, or null if the move is invalid.
     * @throws IllegalStateException If the game is already over.
     */
    override fun play(coordinate: Coordinate): Reversi?{
        if (isGameOver()) throw IllegalStateException("Game is over")
        if (coordinate !in this.validTargets) return null

        val reversiAfterMove = this.copy(
            pieces = pieces + (coordinate to currentPlayer)
        ).flipOpponentPieces(coordinate)

        val newGameState = when {
            reversiAfterMove.isBoardFull() -> reversiAfterMove.result()
            else -> GameState.Run
        }

        return reversiAfterMove.copy(
            gameState = newGameState,
            currentPlayer = currentPlayer.other()
        )
    }

    /**
     * Handles the action of passing the turn in the Reversi game.
     * The current player can pass their turn only if they have no valid moves available.
     *
     * @return A new Reversi instance with the updated state after the pass action, or null if passing is not possible.
     * @throws IllegalStateException If the game is already over.
     */
    override fun pass(): Reversi? {
        if (isGameOver())
            throw IllegalStateException("Game is over")
        if (!canPass())
            return null
        return if (gameState is GameState.Pass){
            this.copy(
                gameState = result()
            )
        }else {
            this.copy(
                currentPlayer = this.currentPlayer.other(),
                gameState = GameState.Pass(this.currentPlayer)
            )
        }
    }
}


/**
 * Checks if the game board is full.
 *
 * @return True if the board is full, false otherwise.
 */
fun Reversi.isBoardFull() = pieces.size == BOARD_SIZE

/**
 * Counts the number of pieces on the board of the specified color.
 * @param color The color of the pieces to count.
 * @return The number of pieces on the board that match the specified color.
 */
fun Reversi.countPieces(color: PiecesColor) = pieces.values.count{it == color}

/**
 * Returns the total number of pieces currently on the board.
 * @return The total number of pieces on the board.
 */
fun Reversi.countPieces() = pieces.size

/**
 * Determines if the specified coordinate is a valid target for the current player's move in the Reversi game.
 *
 * A target is considered valid if it is empty, and placing a piece there results in capturing at least
 * one of the opponent's pieces by surrounding them horizontally, vertically, or diagonally.
 *
 * @param target The coordinate to be checked for validity as a target.
 * @return True if the coordinate is a valid target, false otherwise.
 */
fun Reversi.isValidTarget(target: Coordinate): Boolean {
    if (pieces.containsKey(target)) return false

    val playerColor = currentPlayer
    val opponentColor = playerColor.other()

    return Direction.entries.any { dir ->
        var cur = dir.move(target)
        var seenOpp = false
        var isValid = false

        while (cur != null) {
            val pieceColor = this[cur] ?: break
            if (pieceColor == opponentColor) {
                seenOpp = true
                cur = dir.move(cur)
            } else {
                isValid = seenOpp
                break
            }
        }
        isValid
    }
}

/**
 * Returns a set of coordinates adjacent to the given coordinate that contain no piece.
 * @param coordinate The reference coordinate for which to find empty adjacent coordinates.
 * @return A set of adjacent coordinates that are empty.
 */
fun Reversi.getEmptyAdjacencies(coordinate: Coordinate) = buildSet {
    for (dir in Direction.entries) {
        val current = dir.move(coordinate) ?: continue
        if (this@getEmptyAdjacencies[current] == null) add(current)
    }
}

/**
 * Returns a set of potential target coordinates where the current player
 * can make a move. The potential targets are determined as empty spots adjacent to the opposite player's pieces.
 * @return A set of coordinates representing potential target locations for the current player's move.
 */
fun Reversi.possibleTargets() = buildSet {
    val opponentPieces = pieces.filter { it.value == currentPlayer.other() }
    for ((coordinate, _) in opponentPieces) {
        addAll(getEmptyAdjacencies(coordinate))
    }
}


/**
 * Returns the set of valid target coordinates for the current player's turn
 * in a Reversi game. A valid target is an empty coordinate where placing a piece leads
 * to capturing at least one of the opponent's pieces.
 * @return A set of coordinates representing all valid positions where the current
 *         player can make a move.
 */
fun Reversi.validTargets() = buildSet{
    for(coordinate in possibleTargets()){
        if(isValidTarget(coordinate)) add(coordinate)
    }
}

fun Reversi.flipOpponentPieces(piecePlaced: Coordinate): Reversi{
    var reversi = this
    val playerColor = this[piecePlaced]
    checkNotNull(playerColor)
    val opponentColor = playerColor.other()
    var foundOpp = false

    Direction.entries.forEach { dir ->
        val toFlip = mutableListOf<Coordinate>()
        var cur = dir.move(piecePlaced)

        while (cur != null){
            val curPieceColor = this[cur] ?: break
            if(curPieceColor == opponentColor) {
                foundOpp = true
                toFlip.add(cur)
                cur = dir.move(cur)
            }else{
                if (foundOpp)
                    reversi = reversi.changePiecesColorTo(toFlip.toSet(), playerColor)
                break
            }
        }
    }
    return reversi
}

/**
 * Determines whether the current player can pass their turn in a Reversi game.
 * A player can pass their turn if they do not have any valid moves available.
 * @return `true` if the current player has no valid moves available and can pass their turn;
 *         `false` otherwise.
 */
fun Reversi.canPass() = validTargets.isEmpty()

/**
 * Validates the given board size for a Reversi game.
 * Ensures the board size is within the allowed range and is an even number.
 *
 * @param side The size of the board. Defaults to the predefined constant `BOARD_SIDE`.
 *             Must be between `MIN_SIDE` and `MAX_SIDE` and an even number.
 * @throws IllegalArgumentException If the board size is not within the allowed range
 *                                  or is not an even number.
 */
fun validateBoardSide(side: Int = BOARD_SIDE) {
    require(side in MIN_SIDE..MAX_SIDE) {
        "BOARD_SIDE must be between $MIN_SIDE and $MAX_SIDE."
    }
    require(side % 2 == 0) {
        "BOARD_SIDE must be even."
    }
}
/**
 * Determines the game state based on the current number of black and white pieces on the board.
 *
 * @return The resulting game state:
 *         - [GameState.Draw] if the number of black and white pieces are equal.
 *         - [GameState.Win] with the winning [PiecesColor] if one color has more pieces than the other.
 */
fun Reversi.result(): GameState {
    val black = pieces.values.count { it == PiecesColor.BLACK }
    val white = pieces.values.count { it == PiecesColor.WHITE }
    return when {
        black == white -> GameState.Draw
        black < white  -> GameState.Win(PiecesColor.WHITE)
        else           -> GameState.Win(PiecesColor.BLACK)
    }
}

fun Reversi.isGameOver(): Boolean = gameState is GameState.Win || gameState is GameState.Draw
