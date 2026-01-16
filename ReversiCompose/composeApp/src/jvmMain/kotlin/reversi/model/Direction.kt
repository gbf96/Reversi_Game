package model

/**
 * Represents directions used for movement.
 * @param dx The row offset for the direction.
 * @param dy The column offset for the direction.
 */
enum class Direction(val dx: Int, val dy: Int) {
    N(-1, 0),
    NE(-1, 1),
    E(0, 1),
    SE(1, 1),
    S(1, 0),
    SW(1, -1),
    W(0, -1),
    NW(-1, -1);

    /**
     * Moves the given coordinate by applying the direction's row and column offsets.
     *
     * @param from The initial coordinate to move from.
     * @return The new coordinate after applying the movement, or null if the resulting coordinate is out of bounds.
     */
    fun move(from: Coordinate): Coordinate? =
        from.move( dx,  dy)
}