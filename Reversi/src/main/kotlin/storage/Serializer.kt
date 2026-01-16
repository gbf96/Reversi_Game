package storage

/**
 * Defines the contract for converting structured data objects to a transferable/storable
 * String representation (serialization) and vice-versa (deserialization).
 *
 * This layer separates the data structure definition from its specific storage format.
 *
 * @param Data The type of the data object that needs to be converted to/from a String.
 */
interface Serializer<Data>{
    /**
     * Converts a data object into its string representation.
     *
     * @param d The data object to serialize.
     * @return The resulting string representation.
     */
    fun serialize(d: Data): String

    /**
     * Converts a string representation back into a data object.
     * @param txt The string to deserialize.
     * @return The reconstructed data object.
     */
    fun deserialize(txt: String): Data
}
