package storage

/**
 * Defines the contract for a generic persistence layer that manages data.
 *
 * This interface abstracts the underlying storage mechanism and mandates the core CRUD operations.
 *
 * @param Key The type used to uniquely identify the stored data.
 * @param Data The type of the object being stored or retrieved.
 */
interface Storage<Key, Data> {
    /**
     * Creates a new entry in the storage.
     *
     * @param key the key of the entry.
     * @param data the data to store.
     * @throws IllegalStateException if the key already exists.
     */
    fun create(key: Key, data: Data)

    /**
     * Reads an entry from the storage.
     *
     * @param key the key of the entry.
     * @return the data stored or null if the key does not exist.
     */
    fun read(key: Key): Data?

    /**
     * Updates an entry in the storage.
     *
     * @param key the key of the entry.
     * @param data the new data to store.
     * @throws IllegalStateException if the key does not exist.
     */
    fun update(key: Key, data: Data)

    /**
     * Deletes an entry from the storage.
     *
     * @param key the key of the entry.
     * @throws IllegalStateException if the key does not exist.
     */
    fun delete(key: Key)
}