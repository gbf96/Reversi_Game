package model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CoordinateTests {

    @Test
    fun `create invalid coordinate throws`(){
        assertFailsWith<IllegalArgumentException> { Coordinate(-1,-1) }
        assertFailsWith<IllegalArgumentException> { Coordinate(-1,0) }
        assertFailsWith<IllegalArgumentException> { Coordinate(0,-1) }
        assertFailsWith<IllegalArgumentException> { Coordinate(8,8) }
        assertFailsWith<IllegalArgumentException> { Coordinate(8,7) }
        assertFailsWith<IllegalArgumentException> { Coordinate(7,8) }
    }

    @Test
    fun `check String to Coordinate`() {

        val c1 = "3e".toCoordinateOrNull()
        "3e".toCoordinate()
        val c2 = "E3".toCoordinateOrNull()
        assertNotNull(c1)
        assertNull(c2)
        assertEquals(Coordinate(2, 4), c1)

        assertNull("0a".toCoordinateOrNull())
        assertNull("9a".toCoordinateOrNull())
        assertNull("3z".toCoordinateOrNull())
        assertNull("".toCoordinateOrNull())
        assertNull("33".toCoordinateOrNull())
        assertFailsWith<IllegalArgumentException> {
            "".toCoordinate()
        }
        assertFailsWith<IllegalArgumentException> {
            "9a".toCoordinate()
        }
        assertFailsWith<IllegalArgumentException> {
            "E3".toCoordinate()
        }
        assertFailsWith<IllegalArgumentException> {
            "3z".toCoordinate()
        }
        assertFailsWith<IllegalArgumentException> {
            "33".toCoordinate()
        }
    }

    @Test
    fun `move the coordinate returns the correct coordinate`(){
        var coordinate: Coordinate? = Coordinate(2, 2)
        coordinate = coordinate?.move(1,1)
        assertEquals(Coordinate(3,3), coordinate)
        coordinate = coordinate?.move(4,4)
        assertEquals(Coordinate(7,7), coordinate)
    }

    @Test
    fun `move a coordinate out of the board returns null`(){
        var coordinate: Coordinate? = Coordinate(7, 7)
        assertNotNull(coordinate)
        coordinate = coordinate.move(1,1)
        assertNull(coordinate)
        coordinate = Coordinate(0, 0)
        assertNotNull(coordinate)
        coordinate = coordinate.move(-1,-1)
        assertNull(coordinate)
    }
}