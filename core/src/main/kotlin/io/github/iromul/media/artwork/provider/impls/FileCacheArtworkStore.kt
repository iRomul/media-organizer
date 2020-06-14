package io.github.iromul.media.artwork.provider.impls

import io.github.iromul.media.artwork.file.FileHandledImageFile
import io.github.iromul.media.artwork.file.ImageFile
import io.github.iromul.media.artwork.file.ImageFilesCollection
import io.github.iromul.media.artwork.provider.ArtworkStore
import io.github.iromul.media.commons.fman.FileStorageManager
import io.github.iromul.media.commons.fman.PathKey
import io.github.iromul.media.library.collection.MediaFile
import java.io.File

class FileCacheArtworkStore(
    private val fileStorageManager: FileStorageManager
) : ArtworkStore {

    override fun add(mediaFile: MediaFile, collection: ImageFilesCollection) {
        collection.forEach {
            val filename = "${it.size}.${it.mime.detectExtensionsByMime()}"

            fileStorageManager.createFile(mediaFile.toPathKey(), filename, it.bytes())
        }
    }

    override fun find(mediaFile: MediaFile): ImageFilesCollection {
        return fileStorageManager.getAllFiles(mediaFile.toPathKey())
            .map { it.toImageFile() }
            .let(::ImageFilesCollection)
    }

    private fun MediaFile.toComponents() = listOf(
        if (artist.isBlank()) "Unknown Artist" else artist,
        if (album.isBlank()) "Unknown Album" else album
    )

    private fun MediaFile.toPathKey() = PathKey(toComponents())

    private fun File.toImageFile(): ImageFile {
        val size = nameWithoutExtension.toInt()
        val mime = extension.detectMimeByExtension()

        return FileHandledImageFile(size, mime, this)
    }

    private fun String.detectMimeByExtension(): String {
        return when (this) {
            "jpg", "jpeg" -> "image/jpeg"
            else -> error("Can't resolve mime type for extension $this")
        }
    }

    private fun String.detectExtensionsByMime(): String {
        return when (this) {
            "image/jpeg" -> "jpg"
            else -> error("Can't resolve extension for mime type $this")
        }
    }
}
