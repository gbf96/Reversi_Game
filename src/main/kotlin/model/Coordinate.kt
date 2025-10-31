package model

/**
 * Represents a coordinate in the Reversi board.
 * @param row The row of the coordinate.
 * @param column The column of the coordinate.
 */
@ConsistentCopyVisibility
data class Coordinate private constructor(val row: Int, val column: Int) {

    companion object{
        /**
         * Creates a new zero Coordinate. Validates bounds and throws on invalid input.
         * @param row The zero-based row in 0 until BOARD_SIDE of the coordinate.
         * @param column The zero-based column in 0 until BOARD_SIDEof the coordinate.
         * @return Return a valid coordinate.
         * @throws IllegalArgumentException if the given row or column are not in the valid range.
         */
        operator fun invoke(row: Int, column: Int): Coordinate {
            require (isValidRowAndColumn(row, column)){
            "Coordinates must be in range 0 until $BOARD_SIDE"
            }
            return Coordinate(row, column)
        }
    }
}

/**
 * Checks if the given row and column are valid for a Reversi board.
 * @param row The row to check.
 * @param column The column to check.
 * @return True if the row and column are valid, false otherwise.
 */
private fun isValidRowAndColumn(row: Int, column: Int): Boolean = (row in 0 until BOARD_SIDE && column in 0 until BOARD_SIDE)

/**
 * Creates a coordinate if the given row and column are valid for a Reversi board.
 * @param row The row of the coordinate.
 * @param column The column of the coordinate.
 * @return The coordinate if the row and column are valid, null otherwise.
 */
fun createCoordinateOrNull(row: Int, column: Int): Coordinate? {
    if (!isValidRowAndColumn(row, column)) return null
    return Coordinate(row, column)
}

/**
 * Returns the coordinate obtained by moving the current coordinate in the given direction.
 * @param dRow The row offset.
 * @param dColumn The column offset.
 * @return The coordinate obtained by moving the current coordinate in the given direction, or null if the resulting coordinate is out of bounds.
 */
fun Coordinate.move(dRow: Int, dColumn: Int): Coordinate? = createCoordinateOrNull(row + dRow, column + dColumn)

/**
 * Parses the string to create a `Coordinate` if the string represents a valid board coordinate.
 * A valid board coordinate must be in the format of a single digit followed by a letter,
 * where the digit represents the row and the letter represents the column.
 *
 * @return A `Coordinate` object if the string is a valid board coordinate or `null` if it's invalid.
 */
fun String.toCoordinateOrNull(): Coordinate? {
    if (this.length != 2) return null

    val rowChar = this[0]
    val colChar = this[1].lowercaseChar()

    var row = rowChar.digitToIntOrNull() ?: return null
    row--

    val col = if (colChar in 'a' until 'a' + BOARD_SIDE) colChar - 'a' else return null

    return createCoordinateOrNull(row, col)
}
