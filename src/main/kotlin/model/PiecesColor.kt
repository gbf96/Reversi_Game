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

    companion object {
        /**
         * A mapping of characters to the corresponding `PiecesColor` enum values.
         * This allows efficient lookup of `PiecesColor` values based on their character
         */
        private val bySymbol: Map<Char, PiecesColor> =
            entries.associateBy { it.symbol }

        /**
         * Converts the character to a `PiecesColor` instance if it matches a valid symbol, or returns null otherwise.
         *
         * @return The corresponding `PiecesColor` for the character if it matches a valid symbol,
         *         or null if the character does not match any `PiecesColor`.
         */
        fun Char.toPieceColorOrNull(): PiecesColor? = bySymbol[this]

        /**
         * Converts the string to a `PiecesColor` instance if it represents a valid symbol
         * or returns null otherwise.
         *
         * @return The corresponding `PiecesColor` for the string if it has a single character
         *         matching a valid symbol (# or @), or null if the string is not a valid representation.
         */
        fun String.toPieceColorOrNull(): PiecesColor? =
            if (this.length == 1) this[0].toPieceColorOrNull() else null

    }
}