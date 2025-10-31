package storage

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

/**
* Implementation of the [Storage] interface that persists data by saving it
* as individual text files within a specified base directory on the local file system.
*
* This class uses the [Serializer] contract to convert complex data objects into a
* storable String format (and vice versa) and leverages the Okio library for efficient
* file manipulation.
*
* @param Key The type used to uniquely identify the data entry, used as the filename).
* @param Data The type of the object being stored or retrieved.
* @property baseDirectory The root folder where all data files will be stored.
* @property serializer The implementation responsible for converting the [Data] object
* to and from a String representation.
*/
class TextFileStorage<Key, Data>(
    baseDirectory: String,
    val serializer: Serializer<Data>
): Storage<Key, Data> {

    val fs = FileSystem.SYSTEM
    val basePath: Path = baseDirectory.toPath()

    init {
        with(basePath) {
            if (!exists()) createDirectory()
            else check(isDirectory())
        }
    }

    /**
     * Checks if the current path points to a directory.
     *
     * @return True if it is a directory, False otherwise (including if it does not exist).
     */
    private fun Path.isDirectory() = fs.metadata(this).isDirectory

    /**
     * Creates a new directory at the specified path.
     *
     * The operation is delegated to the 'createDirectory' method of the 'fs' interface.
     */
    private fun Path.createDirectory() = fs.createDirectory(this)

    /**
     * Checks if the item (file or directory) at the current path exists in the file system.
     *
     * @return True if the item exists, False otherwise.
     */
    private fun Path.exists() = fs.exists(this)

    /**
    * Writes a serialized data object as text to the file specified by the path.
    *
    * The [data] object is first serialized to a UTF-8 string using 'serializer'
    * and then written to the file via the 'fs' interface.
    *
    * @param data The data object to be serialized and written.
    */
    private fun Path.writeText(data: Data): Unit{
        parent?.let {
            fs.createDirectories(it)
        }
        fs.write(this){ writeUtf8(serializer.serialize(data)) }
    }

    /**
    * Deletes the item (file or directory) at the current path from the file system.
    *
    * The deletion is delegated to the 'delete' method of the 'fs' interface.
    */
    private fun Path.delete(): Unit = fs.delete(this)

    /**
     * Reads the entire text content from the file at the current path and then
     * deserializes it back into a [Data] object.
     *
     * @return The deserialized [Data] object, or null if reading fails or the path does not exist.
     */
    private fun Path.readText(): Data? =
        fs.read(this){serializer.deserialize(this.readUtf8()) }

    /**
     * Helper function to safely construct the full file path for a given [key]
     * and execute a provided function block [fx] on that generated path.
     *
     * This abstracts path logic, simplifying CRUD operations.
     *
     * @param key The identifier used to construct the filename (e.g., "$key.txt").
     * @param fx The function block to execute, where the generated Path is the receiver ('this').
     * @return The result of the executed block [fx].
     */
    private fun <R>withPath(key: Key, fx: Path.()->R): R =
        (basePath / "$key.txt").fx()


    /**
     * Creates a new entry in the storage by writing the data to a file.
     *
     * @param key the unique key (filename) of the entry.
     * @param data the data object to serialize and store.
     * @throws IllegalStateException if a file for the given [key] already exists.
     */
    override fun create(key: Key, data: Data) = withPath(key){
        check( !exists()){"File already exists"}
        writeText(data)
    }

    /**
     * Reads an entry from the storage.
     *
     * @param key the unique key (filename) of the entry to retrieve.
     * @return the deserialized data object if the file exists, or null if the key is not found.
     */
    override fun read(key: Key): Data? = withPath(key){
        if(!exists()) null
        else readText()
    }

    /**
     * Updates an existing entry in the storage by overwriting the file content.
     *
     * @param key the unique key (filename) of the entry to update.
     * @param data the new data object to store.
     * @throws IllegalStateException if a file for the given [key] does not exist.
     */
    override fun update(key: Key, data: Data) = withPath(key){
        check( exists()){"File $key does not exist"}
        writeText(data)
    }

    /**
     * Deletes an entry (file) from the storage.
     *
     * @param key the unique key (filename) of the entry to delete.
     * @throws IllegalStateException if a file for the given [key] does not exist.
     */
    override fun delete(key: Key) = withPath(key){
        check( exists()){"File $key does not exist"}
        delete()
    }
}
