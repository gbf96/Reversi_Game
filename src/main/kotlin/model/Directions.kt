package model

enum class Direction(val dx: Int, val dy: Int) {
    N(-1, 0),
    NE(-1, 1),
    E(0, 1),
    SE(1, 1),
    S(1, 0),
    SW(1, -1),
    W(0, -1),
    NW(-1, -1);

    fun move(from: Coordinate): Coordinate? =
        from.move( dx,  dy)
}