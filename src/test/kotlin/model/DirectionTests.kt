package model

import kotlin.test.Test
import kotlin.test.assertEquals

class DirectionTests {
    @Test
    fun `move delegates to Coordinate move using its dx dy`() {
        val from = Coordinate(5, 5)
        Direction.entries.forEach { dir ->
            val expected = Coordinate(from.row + dir.dx, from.column + dir.dy)
            assertEquals(expected, dir.move(from))
        }
    }
}