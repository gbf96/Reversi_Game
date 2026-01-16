package model

import storage.GameSerializer
import reversi.storage.TextFileStorage
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


class ClashTests {

    private val storage = TextFileStorage<Name, Reversi>("test", GameSerializer)
    private val gameName = Name("testGame")
    private val playerBlack = PiecesColor.BLACK
    private val playerWhite = PiecesColor.WHITE

    @AfterTest
    fun after() {
        storage.fs.delete(storage.basePath)
    }

    @Test
    fun `start creates a new game and a save in the storage with the name given`() {
        val clash = Clash.start(gameName, storage, playerBlack)

        assertEquals(gameName, clash.name)
        assertEquals(playerBlack, clash.sidePlayer)
        assertEquals(playerBlack, clash.reversi.currentPlayer)

        val loadedReversi = storage.read(gameName)
        assertNotNull(loadedReversi)
        assertEquals(playerBlack, loadedReversi.currentPlayer)
        assertEquals(Reversi(playerBlack).pieces,loadedReversi.pieces)

        storage.delete(gameName)
    }

    @Test
    fun `start fails when file with the name already exists`() {
        Clash.start(gameName, storage, playerBlack)

        assertFailsWith<IllegalArgumentException> {
            Clash.start(gameName, storage, playerWhite)
        }

        storage.delete(gameName)
    }

    @Test
    fun `join fails when game does not exist `() {
        assertFailsWith<IllegalArgumentException> {
            Clash.join(gameName, storage)
        }
    }

    @Test
    fun `joins as the 'other' player (WHITE) when the game is new (4 pieces)`() {

        val newGame = Reversi(playerBlack)
        storage.create(gameName, newGame)

        val clash = Clash.join(gameName, storage)

        assertEquals(gameName, clash.name)
        assertEquals(playerWhite, clash.sidePlayer)
        assertEquals(newGame, clash.reversi)
        storage.delete(gameName)
    }

    @Test
    fun `joins as the current player (BLACK) if P1 already played (5 pieces)`() {
        val game = Reversi(playerWhite)
        val gameAfterOneMove = game.play("6d".toCoordinateOrNull() ?: error("valid coordinate")) ?: error("valid play")
        storage.create(gameName, gameAfterOneMove)
        println(gameAfterOneMove.currentPlayer)
        val clash = Clash.join(gameName, storage)

        assertEquals(playerBlack, clash.sidePlayer)
        storage.delete(gameName)
    }

    @Test
    fun `join fails if two players played`() {
        var game = Reversi(playerWhite)
        game = game.play("6d".toCoordinate()) ?: error("valid play")
        game = game.play("4c".toCoordinate()) ?: error("valid play")
        storage.create(gameName, game)

        assertFailsWith<IllegalStateException>{
            Clash.join(gameName, storage)
        }
        storage.delete(gameName)
    }


    @Test
    fun `valid play succeeds and updates the storage`() {
        val baseReversi = Reversi(PiecesColor.BLACK)
        val clash = Clash(storage, gameName, playerBlack, baseReversi, false)
        storage.create(gameName, baseReversi)

        val newClash = clash.play("3d".toCoordinate()) ?: error("valid play")

        assertNotNull(newClash)
        assertEquals(playerWhite, newClash.reversi.currentPlayer)
        assertEquals(newClash.reversi, storage.read(gameName))
        storage.delete(gameName)
    }

    @Test
    fun `invalid play returns null`() {
        val baseReversi = Reversi(PiecesColor.BLACK)
        val clash = Clash(storage, gameName, playerBlack, baseReversi, false)

        val newClash = clash.play("6d".toCoordinate())

        assertNull(newClash)
    }

    @Test
    fun `play fails if is not the player turn`() {
        val clash = Clash(storage, gameName, playerWhite, Reversi(PiecesColor.BLACK), false)

        assertFailsWith<IllegalStateException>{
            clash.play("6d".toCoordinateOrNull() ?: error("valid coordinate")) ?: error("valid play")
        }
    }



    @Test
    fun `when players do not have available moves and both pass, the game ends`() {
        val moves = listOf("3d", "3e", "4f", "3g", "3f", "5c", "3h", "2f", "4c", "3c", "2e", "1e", "3b", "4h", "5h", "3a")

        var clashBlack = Clash.start(gameName, storage, PiecesColor.BLACK)

        var clashWhite = Clash.join(gameName, storage)

        moves.forEachIndexed { index, moveStr ->
            val coord = moveStr.toCoordinateOrNull()
                ?: error("Coordinate valid in test returned null")

            if (index % 2 == 0) {
                clashBlack = clashBlack.play(coord)
                    ?: error("Valid play by BLACK failed at $moveStr")
                clashWhite = clashWhite.refresh()
            } else {
                clashWhite = clashWhite.play(coord)
                    ?: error("Valid play by WHITE failed at $moveStr")
                clashBlack = clashBlack.refresh()
            }
        }

        assertEquals(PiecesColor.BLACK, clashBlack.reversi.currentPlayer)
        assertEquals(PiecesColor.BLACK, clashWhite.reversi.currentPlayer)

        clashBlack = clashBlack.pass() ?: error("Valid pass by BLACK returned null")

        clashWhite = clashWhite.refresh()

        assertTrue(clashWhite.reversi.gameState is GameState.Pass)
        assertEquals(PiecesColor.WHITE, clashWhite.reversi.currentPlayer)
        assertTrue(clashBlack.reversi.gameState is GameState.Pass)
        assertEquals(PiecesColor.WHITE, clashBlack.reversi.currentPlayer)

        clashWhite = clashWhite.pass() ?: error("Valid pass by WHITE returned null")

        assertEquals(GameState.Win(PiecesColor.WHITE), clashWhite.reversi.gameState)
        clashBlack = clashBlack.refresh()
        assertEquals(GameState.Win(PiecesColor.WHITE), clashBlack.reversi.gameState)
        storage.delete(gameName)
    }

    @Test
    fun `trying to refresh, play or pass in ended game fails`() {
        val moves = listOf("3d", "3e", "4f", "3g", "3f", "5c", "3h", "2f", "4c", "3c", "2e", "1e", "3b", "4h", "5h", "3a")

        var clashBlack = Clash.start(gameName, storage, PiecesColor.BLACK)

        var clashWhite = Clash.join(gameName, storage)

        moves.forEachIndexed { index, moveStr ->
            val coord = moveStr.toCoordinateOrNull()
                ?: error("Coordinate valid in test returned null")

            if (index % 2 == 0) {
                clashBlack = clashBlack.play(coord)
                    ?: error("Valid play by BLACK failed at $moveStr")
                clashWhite = clashWhite.refresh()
            } else {
                clashWhite = clashWhite.play(coord)
                    ?: error("Valid play by WHITE failed at $moveStr")
                clashBlack = clashBlack.refresh()
            }
        }
        clashBlack = clashBlack.pass() ?: error("Valid pass by BLACK returned null")
        clashWhite = clashWhite.refresh()
        clashWhite = clashWhite.pass() ?: error("Valid pass by WHITE returned null")
        clashBlack = clashBlack.refresh()
       assertFailsWith <IllegalStateException>{
           clashBlack.refresh()
       }
        assertFailsWith <IllegalStateException>{
            clashWhite.refresh()
        }
        assertFailsWith <IllegalStateException>{
             clashBlack.pass()
        }
        assertFailsWith <IllegalStateException>{
            clashWhite.pass()
        }
        assertFailsWith <IllegalStateException>{
            clashBlack.play("3d".toCoordinateOrNull()
                ?: error("Coordinate valid in test returned null"))
        }
        assertFailsWith <IllegalStateException>{
            clashWhite.play("3d".toCoordinateOrNull()
                ?: error("Coordinate valid in test returned null"))
        }
        storage.delete(gameName)
    }


    @Test
    fun `pass returns null when players have available moves and try to pass `() {


        val clashBlack = Clash.start(gameName, storage, PiecesColor.BLACK)

        assertNull(clashBlack.pass())
        storage.delete(gameName)
    }

    @Test
    fun `pass fails if is not the player turn`() {
        val clash = Clash(storage, gameName, playerWhite, Reversi(playerBlack), false)

        assertFailsWith<IllegalStateException>{
            clash.pass()
        }

    }

    @Test
    fun `refresh update the application with the storage state`() {
        val clash = Clash(storage, gameName, playerBlack, Reversi(playerBlack), false)

        val stateAfterOpponentMove = Reversi(playerWhite)
        storage.create(gameName, stateAfterOpponentMove)

        val refreshedClash = clash.refresh()

        assertEquals(stateAfterOpponentMove, refreshedClash.reversi)
        assertEquals(playerBlack, refreshedClash.sidePlayer)

        storage.delete(gameName)
    }

    @Test
    fun `refresh fails if the game file does not exist`() {
        val clash = Clash(storage, gameName, playerBlack, Reversi(playerBlack), false)

        assertFailsWith< IllegalArgumentException> {
            clash.refresh()
        }
    }
}