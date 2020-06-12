package io.github.iromul.media.library

import io.github.iromul.media.library.collection.MediaCollection
import io.github.iromul.media.library.collection.MediaCollectionType
import io.github.iromul.media.library.collection.MediaCollectionType.*
import io.github.iromul.media.library.layout.MediaCollectionLayout
import java.io.File

class MediaLibrary(
    private val mediaRoot: File,
    private val layout: MediaCollectionLayout
) {

    fun forEachCollection(action: (MediaCollection) -> Unit) {
        val libraryDirectories = listOf(File(mediaRoot, "Artists"), File(mediaRoot, "Playlists"))

        libraryDirectories.forEach { subdirectory ->
            subdirectory.walkTopDown()
                .filter { it.isDirectory && it.isMediaCollection }
                .forEach {
                    val type = when {
                        layout.isPlaylist(it) -> PLAYLIST
                        layout.isAlbum(it) -> ALBUM
                        layout.isArtistEssential(it) -> ARTIST_ESSENTIAL_PLAYLIST
                        else -> throw IllegalStateException("Unsupported collection type")
                    }

                    action(it.toMediaCollection(type))
                }
        }
    }

    private fun File.toMediaCollection(type: MediaCollectionType): MediaCollection {
        return MediaCollection(
            name = name,
            directory = this,
            type = type,
            files = getMediaFiles()
        )
    }

    private fun File.getMediaFiles(): List<File> {
        val array = listFiles { _, name ->
            name.matches(".*\\.(mp3|aac|m4a)".toRegex())
        } ?: throw IllegalStateException("'${path}' is not readable directory")

        return array.toList()
    }

    private val File.isMediaCollection: Boolean
        get() = getMediaFiles().isNotEmpty()
}
