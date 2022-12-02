package io.github.iromul.media.library

import io.github.iromul.media.library.collection.MediaCollection
import io.github.iromul.media.library.collection.MediaCollectionType
import io.github.iromul.media.library.collection.MediaFile
import io.github.iromul.media.library.layout.MediaCollectionLayout
import io.github.iromul.media.library.layout.MediaCollectionTypeHandler
import org.jaudiotagger.audio.AudioFileIO
import java.io.File

class MediaLibraryLoader {

    fun load(mediaRoot: File, layout: MediaCollectionLayout): MediaLibrary {
        val mediaCollections = mediaRoot.walkTopDown()
            .filter { it.isDirectory }
            .filter { layout.isMediaCollectionDirectory(it) }
            .map {
                val typeMatcher = layout.findMediaCollectionTypeHandler(it)!!

                val mediaFiles = it.walkMediaFiles(typeMatcher).toList()

                MediaCollection(it.name, it, typeMatcher.toMediaCollectionType(), mediaFiles)
            }
            .toList()

        return MediaLibrary(mediaRoot, mediaCollections)
    }

    private fun MediaCollectionTypeHandler.toMediaCollectionType() = MediaCollectionType(name, order)

    private fun File.walkMediaFiles(typeHandler: MediaCollectionTypeHandler): Sequence<MediaFile> {
        return walkTopDown()
            .filter { it.isFile }
            .filter { typeHandler.mediaFilesMatcher(it) }
            .map { it.loadMediaFile() }
    }

    private fun File.loadMediaFile(): MediaFile {
        val audioFile = AudioFileIO.read(this)

        return MediaFile(this, audioFile)
    }
}