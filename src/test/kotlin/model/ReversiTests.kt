package model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReversiTests {

    @Test
    fun `create Reversi with player succeeds`(){
        val player = PiecesColor.BLACK
        Reversi(playerOne = player)
    }

    @Test
    fun `invalid board size throws`(){
        assertFailsWith <IllegalArgumentException>{
            validateBoardSide(3)
        }
        assertFailsWith <IllegalArgumentException>{
            validateBoardSide(5)
        }
        assertFailsWith <IllegalArgumentException>{
            validateBoardSide(27)
        }
    }

    @Test
    fun `initially the Reversi pieces are at the correct positions`() {
        val player = PiecesColor.BLACK
        val sut = Reversi(playerOne = player)
        val mid = BOARD_SIDE / 2 - 1

        val expectedBlacks = setOf(
            Coordinate(mid + 1, mid),
            Coordinate(mid, mid + 1)
        )
        val expectedWhites = setOf(
            Coordinate(mid, mid),
            Coordinate(mid + 1, mid + 1)
        )

        expectedBlacks.forEach { c ->
            assertEquals(PiecesColor.BLACK, sut[c])
        }
        expectedWhites.forEach { c ->
            assertEquals(PiecesColor.WHITE, sut[c])
        }

        for (r in 0 until BOARD_SIDE) {
            for (q in 0 until BOARD_SIDE) {
                val c = Coordinate(r, q)
                if (c !in expectedBlacks && c !in expectedWhites) {
                    assertNull(sut[c])
                }
            }
        }
        assertEquals(4,sut.countPieces())
        assertEquals(2,sut.countPieces(PiecesColor.BLACK))
        assertEquals(2,sut.countPieces(PiecesColor.WHITE))
        assertEquals(sut, sut.reversi)
    }

    @Test
    fun `change a color of a null piece in the puzzle throws`(){
        val player = PiecesColor.BLACK
        var sut = Reversi(playerOne = player)
        assertFailsWith<IllegalArgumentException> {
            sut = sut.changePieceColor(Coordinate(0, 0))
        }
    }

    @Test
    fun `change a color of a valid piece in the puzzle succeeds`(){
        val player = PiecesColor.BLACK
        var sut = Reversi(playerOne = player)
        val mid = BOARD_SIDE / 2 - 1
        val centerWhite = Coordinate(mid, mid) // initially WHITE

        assertEquals(PiecesColor.WHITE, sut[centerWhite])

        sut = sut.changePieceColor(centerWhite)

        assertEquals(PiecesColor.BLACK, sut[centerWhite])
    }

    @Test
    fun `check the valid targets`(){
        val reversi = Reversi(PiecesColor.WHITE)
        val set = setOf(
            Coordinate(2, 4),
            Coordinate(3, 5),
            Coordinate(4, 2),
            Coordinate(5, 3)
        )
        assertEquals(set, reversi.validTargets)
    }

    @Test
    fun `check the empty adjacencies`(){
        val reversi = Reversi(PiecesColor.WHITE)
        val coordinate = Coordinate(4, 3)
        val set = setOf(
            Coordinate(3, 2),
            Coordinate(4, 2),
            Coordinate(5, 2),
            Coordinate(5, 3),
            Coordinate(5, 4)

        )
        assertEquals(set, reversi.getEmptyAdjacencies(coordinate))
    }

    @Test
    fun `play returns null on illegal move`() {
        val sut = Reversi(playerOne = PiecesColor.BLACK)
        val illegal = Coordinate(0, 0)
        assertFalse(sut.isValidTarget(illegal))

        val after = sut.play(illegal)
        assertNull(after)
    }



    @Test
    fun `play switches turn to the opponent and place the piece after a legal move`(){
        var reversi: Reversi? = Reversi(PiecesColor.WHITE)
        val input = "3e"
        val coordinate = input.toCoordinateOrNull()
        assertNotNull(coordinate)
        assertNotNull(reversi)
        reversi = reversi.play(coordinate)
        assertNotNull(reversi)
        assertEquals(PiecesColor.BLACK, reversi.currentPlayer)
        assertEquals(PiecesColor.WHITE, reversi[coordinate])
    }

    @Test
    fun `pieces already in board are invalid targets`(){
        val reversi = Reversi(PiecesColor.WHITE)
        assertFalse(reversi.isValidTarget(Coordinate(3,3)))
    }

    @Test
    fun `play ends game with BLACK win when last empty is filled`() {
        val last = Coordinate(0, 0)
        val almostFull = buildMap {
            for (r in 0 until BOARD_SIDE) {
                for (c in 0 until BOARD_SIDE) {
                    val coord = Coordinate(r, c)
                    if (coord != last) put(coord, if (r  % 2 == 0) PiecesColor.BLACK else PiecesColor.WHITE)
                }
            }
        }
        val current = PiecesColor.BLACK
        val sut = Reversi(currentPlayer = current, pieces = almostFull, gameState = GameState.Run)

        assertNull(sut[last])

        assertTrue(sut.isValidTarget(last))

        val after = sut.play(last)

        assertNotNull(after)

        assertEquals(current, after[last])

        val state = after.gameState
        assertTrue(state is GameState.Win)
        assertEquals(PiecesColor.BLACK, state.winner)
    }



    @Test
    fun `play ends game with WHITE win when last empty is filled`() {
        val last = Coordinate(0, 0)
        val almostFull = buildMap {
            for (r in 0 until BOARD_SIDE) {
                for (c in 0 until BOARD_SIDE) {
                    val coord = Coordinate(r, c)
                    if (coord != last) {
                        put(coord, if (r  % 2 == 0) PiecesColor.WHITE else PiecesColor.BLACK)
                    }
                }
            }
        }
        val current = PiecesColor.WHITE
        val sut = Reversi(currentPlayer = current, pieces = almostFull, gameState = GameState.Run)

        assertNull(sut[last])
        assertTrue(sut.isValidTarget(last))

        val after = sut.play(last)


        assertNotNull(after)

        assertEquals(current, after[last])

        val state = after.gameState
        assertTrue(state is GameState.Win)
        assertEquals(PiecesColor.WHITE, state.winner)
    }

    @Test
    fun `play ends game with DRAW when last empty is filled`() {
        val last = Coordinate(0, 0)
        val rowOneColumnSeven = Coordinate(0,7)
        val almostFull = buildMap {
            for (r in 0 until BOARD_SIDE) {
                for (c in 0 until BOARD_SIDE) {
                    val coord = Coordinate(r, c)
                    if (coord != last && r != 0) {
                        put(coord, if (r in listOf(1,4,5)) PiecesColor.WHITE else PiecesColor.BLACK)
                    }else if(coord != last){
                        put(coord, PiecesColor.BLACK)
                    }
                    put(rowOneColumnSeven, PiecesColor.WHITE)
                }
            }
        }
        val current = PiecesColor.WHITE
        val sut = Reversi(currentPlayer = current, pieces = almostFull, gameState = GameState.Run)
        assertNull(sut[last])
        assertTrue(sut.isValidTarget(last))

        val after = sut.play(last)


        assertNotNull(after)
        assertEquals(current, after[last])

        val state = after.gameState
        assertTrue(state is GameState.Draw)
    }

    @Test
    fun `Multiple plays results in the expected board`(){
        var reversi = Reversi(PiecesColor.BLACK)
        val moves = listOf("3d", "3e", "4f", "3g", "3f", "5c", "3h", "2f", "4c", "3c", "2e", "1e", "3b", "4h", "5h", "3a")
        moves.forEach {
            reversi = reversi.play(
                it.toCoordinateOrNull()
                    ?: error("Coordinate valid returned null"))
                ?: error("Valid play returned null"
                )
        }
        val expectedMap = mapOf(
            Coordinate(0, 4) to PiecesColor.WHITE,
            Coordinate(1, 4) to PiecesColor.WHITE,
            Coordinate(1, 5) to PiecesColor.WHITE,
            Coordinate(2, 0) to PiecesColor.WHITE,
            Coordinate(2, 1) to PiecesColor.WHITE,
            Coordinate(2, 2) to PiecesColor.WHITE,
            Coordinate(2, 3) to PiecesColor.WHITE,
            Coordinate(2, 4) to PiecesColor.WHITE,
            Coordinate(2, 5) to PiecesColor.WHITE,
            Coordinate(2, 6) to PiecesColor.WHITE,
            Coordinate(2, 7) to PiecesColor.BLACK,
            Coordinate(3, 2) to PiecesColor.WHITE,
            Coordinate(3, 3) to PiecesColor.WHITE,
            Coordinate(3, 4) to PiecesColor.WHITE,
            Coordinate(3, 5) to PiecesColor.WHITE,
            Coordinate(3, 7) to PiecesColor.BLACK,
            Coordinate(4, 2) to PiecesColor.WHITE,
            Coordinate(4, 3) to PiecesColor.WHITE,
            Coordinate(4, 4) to PiecesColor.WHITE,
            Coordinate(4, 7) to PiecesColor.BLACK,
        )
        assertEquals(expectedMap, reversi.pieces)
    }

    @Test
    fun `when players do not have available moves and both pass, the game ends`() {
        var reversi = Reversi(PiecesColor.BLACK)
        val moves = listOf("3d", "3e", "4f", "3g", "3f", "5c", "3h", "2f", "4c", "3c", "2e", "1e", "3b", "4h", "5h", "3a")
        moves.forEach {
            reversi = reversi.play(
                it.toCoordinateOrNull()
                    ?: error("Coordinate valid returned null"))
                ?: error("Valid play returned null"
                )
        }
        assertEquals(PiecesColor.BLACK, reversi.currentPlayer)

        reversi = reversi.pass() ?: error("Valid pass returned null")

        assertTrue(reversi.gameState is GameState.Pass)
        assertEquals(PiecesColor.WHITE, reversi.currentPlayer)

        reversi = reversi.pass() ?: error("Valid pass returned null")
        assertEquals(reversi.gameState, GameState.Win(PiecesColor.WHITE))
    }

    @Test
    fun `game continues Run when board not full and both players still have moves`() {
        val sut = Reversi(playerOne = PiecesColor.BLACK)
        val move = "3d".toCoordinateOrNull() ?: error("invalid test coordinate")
        assertTrue(sut.isValidTarget(move))

        val after = sut.play(move)
        assertNotNull(after)
        assertTrue(after.gameState is GameState.Run)
    }



    @Test
    fun `play keeps game running when board not full and moves remain`() {
        val sut = Reversi(playerOne = PiecesColor.BLACK)
        val move = "3d".toCoordinateOrNull()
        assertNotNull(move)
        assertTrue(sut.isValidTarget(move))

        val after = sut.play(move)
        assertNotNull(after)
        assertEquals(PiecesColor.WHITE, after.currentPlayer)
        assertEquals(PiecesColor.BLACK, after[move])
        assertTrue(after.gameState is GameState.Run)
    }
}