package storage

import model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GameSerializerTest {

    private fun serializePiecesToSet(pieces: Map<Coordinate, PiecesColor>): Set<String> {
        return pieces.entries.map { (coord, color) ->
            val rowNum = coord.row + 1
            val colChar = 'A' + coord.column
            val colorToken = if (color == PiecesColor.BLACK) "0" else "1"
            "$rowNum$colChar:$colorToken"
        }.toSet()
    }

    @Test
    fun `serialize a game in Run state`() {
        val game = Reversi(PiecesColor.BLACK)
        val serializedString = GameSerializer.serialize(game)
        val lines = serializedString.lines()

        val expectedPieces = serializePiecesToSet(game.pieces)
        val actualPieces = lines[1].split(' ').filter { it.isNotBlank() }.toSet()
        println(lines[0])
        println(lines[1])


        assertEquals("Run:0", lines[0], "State line should indicate RUN state for BLACK player")
        assertEquals(expectedPieces, actualPieces, "Pieces line should contain all initial pieces correctly serialized")
    }

    @Test
    fun `serialize a game in Pass state`() {
        val initialPieces = buildMap {
            put(Coordinate(4, 3), PiecesColor.WHITE) // 5D
            put(Coordinate(3, 4), PiecesColor.WHITE) // 4E
            put(Coordinate(3, 3), PiecesColor.WHITE) // 4D
            put(Coordinate(4, 4), PiecesColor.WHITE) // 5E
        }
        val game = Reversi(PiecesColor.BLACK, GameState.Pass(PiecesColor.BLACK), initialPieces)
        val serializedString = GameSerializer.serialize(game)
        val lines = serializedString.lines()

        val expectedPieces = serializePiecesToSet(game.pieces)
        val actualPieces = lines[1].split(' ').filter { it.isNotBlank() }.toSet()

        assertEquals("Pass:0", lines[0], "State line should indicate PASS state for BLACK player")
        assertEquals(expectedPieces, actualPieces, "Pieces line should contain all initial pieces correctly serialized")
    }

    @Test
    fun `serialize a game in Win state`() {
        val initialPieces = buildMap {
            put(Coordinate(4, 3), PiecesColor.WHITE) // 5D
            put(Coordinate(3, 4), PiecesColor.WHITE) // 4E
            put(Coordinate(3, 3), PiecesColor.WHITE) // 4D
            put(Coordinate(4, 4), PiecesColor.WHITE) // 5E
        }
        val game = Reversi(PiecesColor.WHITE, GameState.Win(PiecesColor.WHITE), initialPieces)
        val serializedString = GameSerializer.serialize(game)
        val lines = serializedString.lines()

        val expectedPieces = serializePiecesToSet(initialPieces)
        val actualPieces = lines[1].split(' ').filter { it.isNotBlank() }.toSet()

        assertEquals("Win:1", lines[0], "State line should indicate WIN state for WHITE player")
        assertEquals(expectedPieces, actualPieces, "Pieces line should be correctly serialized")
    }

    @Test
    fun `serialize a game in Draw state with empty board`() {
        val game = Reversi(PiecesColor.BLACK, GameState.Draw, emptyMap())
        val serializedString = GameSerializer.serialize(game)
        val lines = serializedString.lines()

        assertEquals("Draw", lines[0], "State line should indicate DRAW state")
        assertTrue(lines.getOrElse(1) { "" }.isEmpty(), "Pieces line should be empty for a draw with no pieces")
    }

    @Test
    fun `deserialize a game in Run state`() {
        val serializedString = "Run:0\n4D:1 4E:0 5D:0 5E:1"
        val game = GameSerializer.deserialize(serializedString)

        val expectedPieces = buildMap {
            put(Coordinate(4, 3), PiecesColor.BLACK) // 5D
            put(Coordinate(3, 4), PiecesColor.BLACK) // 4E
            put(Coordinate(3, 3), PiecesColor.WHITE) // 4D
            put(Coordinate(4, 4), PiecesColor.WHITE) // 5E
        }

        assertEquals(GameState.Run, game.gameState)
        assertEquals(PiecesColor.BLACK, game.currentPlayer)
        assertEquals(expectedPieces, game.pieces)
    }

    @Test
    fun `deserialize a game in Pass state`() {
        val serializedString = "Pass:0\n4D:1 4E:1 5D:1 5E:1"
        val game = GameSerializer.deserialize(serializedString)

        val expectedPieces = buildMap {
            put(Coordinate(4, 3), PiecesColor.WHITE) // 5D
            put(Coordinate(3, 4), PiecesColor.WHITE) // 4E
            put(Coordinate(3, 3), PiecesColor.WHITE) // 4D
            put(Coordinate(4, 4), PiecesColor.WHITE) // 5E
        }

        assertEquals(GameState.Pass(PiecesColor.BLACK), game.gameState)
        assertEquals(PiecesColor.BLACK, game.currentPlayer)
        assertEquals(expectedPieces, game.pieces)
    }

    @Test
    fun `deserialize a game in Win state`() {
        val serializedString = "Win:1\n4D:1 4E:1 5D:1 5E:1"
        val game = GameSerializer.deserialize(serializedString)

        val expectedPieces = buildMap {
            put(Coordinate(4, 3), PiecesColor.WHITE) // 5D
            put(Coordinate(3, 4), PiecesColor.WHITE) // 4E
            put(Coordinate(3, 3), PiecesColor.WHITE) // 4D
            put(Coordinate(4, 4), PiecesColor.WHITE) // 5E
        }

        assertEquals(GameState.Win(PiecesColor.WHITE), game.gameState)
        assertEquals(PiecesColor.WHITE, game.currentPlayer)
        assertEquals(expectedPieces, game.pieces)
    }

    @Test
    fun `deserialize a game in Draw state with empty board`() {
        //val serializedString = "Draw\n4D:1 4E:0 5D:0 5E:1"
        val serializedString = "Draw\n"
        val game = GameSerializer.deserialize(serializedString)

        val expectedPieces = emptyMap<Coordinate, PiecesColor>()

        assertEquals(GameState.Draw, game.gameState)
        assertEquals(expectedPieces, game.pieces)
    }
}