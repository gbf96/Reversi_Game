package model

import model.PiecesColor.Companion.toPieceColorOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PiecesColorTests {

    @Test
    fun `changes color with other`() {
        val color = PiecesColor.BLACK
        val otherColor = color.other()
        assertEquals(PiecesColor.WHITE, otherColor)
        assertEquals(PiecesColor.BLACK, otherColor.other())
    }

    @Test
    fun `exposes correct symbols and toString`() {
        assertEquals('#', PiecesColor.BLACK.symbol)
        assertEquals('@', PiecesColor.WHITE.symbol)
        assertEquals("#", PiecesColor.BLACK.toString())
        assertEquals("@", PiecesColor.WHITE.toString())
    }

    @Test
    fun `parses valid chars and strings to piece colors`() {
        assertEquals(PiecesColor.BLACK, "#".toPieceColorOrNull())
        assertEquals(PiecesColor.BLACK, '#'.toPieceColorOrNull())
        assertEquals(PiecesColor.WHITE, "@".toPieceColorOrNull())
        assertEquals(PiecesColor.WHITE, '@'.toPieceColorOrNull())
    }

    @Test
    fun `returns null for invalid chars and strings`() {
        assertNull("!".toPieceColorOrNull())
        assertNull('!'.toPieceColorOrNull())
        assertNull("3".toPieceColorOrNull())
        assertNull('3'.toPieceColorOrNull())
        assertNull("".toPieceColorOrNull())
        assertNull('-'.toPieceColorOrNull())
        assertNull("@@".toPieceColorOrNull())
    }
}
