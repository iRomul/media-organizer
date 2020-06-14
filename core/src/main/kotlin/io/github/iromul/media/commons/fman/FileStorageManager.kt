package io.github.iromul.media.commons.fman

import io.github.iromul.media.listFilesSafely
import io.github.iromul.media.sanitizeWindowsFileName
import java.io.File

class FileStorageManager(
    private val root: File
) {

    private val keyDirectoryReadCache = HashMap<PathKey, Collection<File>>()

    fun createFile(key: PathKey, filename: String, data: ByteArray): File {
        keyDirectoryReadCache.remove(key)

        val dir = locateKeyDirectory(key)

        return File(dir, filename).apply {
            createNewFile()
            bufferedWriter().use { writeBytes(data) }
        }
    }

    fun getAllFiles(key: PathKey): Collection<File> {
        return keyDirectoryReadCache.getOrPut(key) {
            val dir = locateKeyDirectory(key)

            dir.listFilesSafely()
        }
    }

    private fun locateKeyDirectory(key: PathKey): File = key.components
        .map(String::sanitizeWindowsFileName)
        .fold(root.toPath()) { path, b -> path.resolve(b) }
        .toFile()
        .apply { mkdirs() }
}
