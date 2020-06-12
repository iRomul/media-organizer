package io.github.iromul.media.artwork

import io.github.iromul.media.sanitizeWindowsFileName
import java.io.File

class ArtworkCache(
    private val root: File
) {

    fun find(artist: String, album: String): Images {
        val components = listOf(artist, album)

        val fullPath = components.fold(root.toPath()) { path, b ->
            path.resolve(b.sanitizeWindowsFileName())
        }

        val targetDirectory = fullPath.toFile()
        targetDirectory.mkdirs()

        return Images(targetDirectory)
    }
}
