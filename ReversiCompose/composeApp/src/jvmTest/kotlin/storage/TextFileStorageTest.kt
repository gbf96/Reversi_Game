package storage

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import reversi.storage.TextFileStorage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TextFileStorageTest {
    private val testSerializer = object : Serializer<String> {
        override fun serialize(d: String): String = d
        override fun deserialize(txt: String): String = txt
    }

    private val fs = FileSystem.SYSTEM
    private val testDirName = "src/test/storage"
    private val testDir: Path = testDirName.toPath()
    private val st = TextFileStorage<String, String>(testDirName, testSerializer)

    private fun cleanupDirectory() {
        if (fs.exists(testDir)) {
            fs.deleteRecursively(testDir)
        }
    }

    @Test
    fun `init creates directory if it does not exist`() {
        cleanupDirectory()
        val storage = TextFileStorage<String, String>(testDirName, testSerializer)
        assertTrue(fs.exists(testDir), "Directory should be created by init block")
        assertTrue(fs.metadata(testDir).isDirectory, "Path should be a directory")
        cleanupDirectory()
    }

    @Test
    fun `init fails if base path is a file`() {
        cleanupDirectory()
        fs.write(testDir) { writeUtf8("I am a file, not a directory") }
        assertFailsWith<IllegalStateException> {
            TextFileStorage<String, String>(testDirName, testSerializer)
        }
        cleanupDirectory()
    }

    @Test
    fun `Create and read file`() {
        val fileName = "test-example"
        val data = "Simple text contents"

        cleanupDirectory()
        st.create(fileName, data)
        val readData = st.read(fileName)
        assertEquals(data, readData)
        cleanupDirectory()

    }

    @Test
    fun `Create and read file in a subdirectory`() {
        val fileName = "subdir/test-example"
        val data = "Simple text contents"

        cleanupDirectory()
        st.create(fileName, data)
        val readData = st.read(fileName)
        assertEquals(data, readData)
        cleanupDirectory()

    }

    @Test
    fun `Create fail file already exists`() {
        val fileName = "test-example"
        val data = "Simple text contents"

        cleanupDirectory()
        st.create(fileName, data)
        assertFailsWith<IllegalStateException> {st.create(fileName, "Another Content")}
        cleanupDirectory()
    }


    @Test
    fun `Read fail file does not exist`() {
        val fileName = "test-example"
        val data = "Simple text contents"

        cleanupDirectory()
        st.create(fileName, data)
        assertNull(st.read("test-example2"))
        cleanupDirectory()
    }

    @Test
    fun `Update file`() {
        val fileName = "test-example"
        val data = "Simple text contents"

        cleanupDirectory()
        st.create(fileName, data)
        st.update(fileName, "New Content")
        val readData = st.read(fileName)
        assertEquals("New Content", readData)
        cleanupDirectory()
    }

    @Test
    fun `Update fail file does not exist`() {
        val fileName = "test-example"
        val data = "Simple text contents"

        cleanupDirectory()
        st.create(fileName, data)
        assertFailsWith<IllegalStateException> {st.update("test-example2", "New Content")}
        cleanupDirectory()
    }

    @Test
    fun `Delete file`() {
        val fileName = "test-example"
        val data = "Simple text contents"

        cleanupDirectory()
        st.create(fileName, data)
        st.delete(fileName)
        assertNull(st.read(fileName))
        cleanupDirectory()
    }

    @Test
    fun `Delete fail file does not exist`() {
        val fileName = "test-example"
        val data = "Simple text contents"

        cleanupDirectory()
        st.create(fileName, data)
        assertFailsWith<IllegalStateException> {st.delete("test-example2")}
        cleanupDirectory()
    }
}