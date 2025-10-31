package model
/**
 * Represents the color of a Reversi player piece that can be either black or white.
 */

enum class PiecesColor(val symbol: Char) {
    BLACK('#'),
    WHITE('@');

    /**
     * Returns the opposite color.
     * @return The opposite color.
     */
    fun other(): PiecesColor = if (this == BLACK) WHITE else BLACK

    /**
     * Returns a string representation of the color.
     * @return A string representation of the color, # to represent black and @ to represent white.
     */
    override fun toString(): String = symbol.toString()

    /**
     * Companion object for the PiecesColor enum, providing utility methods for
     * interpreting characters and strings as PiecesColor values.
     *
     * Creating a map instead of scanning all enum values each time, a character like '#' or '@' needs to be parsed.
     */
    companion object {
        private val bySymbol: Map<Char, PiecesColor> =
            entries.associateBy { it.symbol }

        fun Char.toPieceColorOrNull(): PiecesColor? = bySymbol[this]
        fun String.toPieceColorOrNull(): PiecesColor? =
            if (this.length == 1) this[0].toPieceColorOrNull() else null

    }
}