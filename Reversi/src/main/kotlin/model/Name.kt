package model

/**
 * Represents a validated name value object.
 * @property value The underlying string value of the name after validation.
 * @throws IllegalArgumentException If the name does not meet the validation criteria.
 */
@JvmInline
value class Name(val value: String) {
    init { require( isValid(value) ) { "Invalid name $value" } }
    override fun toString() = value
    companion object {
        private fun isValid(value: String) =
            value.isNotBlank() && value.all { it.isLetterOrDigit() }
    }
}