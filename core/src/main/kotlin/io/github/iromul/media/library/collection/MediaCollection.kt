package io.github.iromul.media.library.collection

import java.io.File

class MediaCollection(
    val name: String,
    val directory: File,
    val type: MediaCollectionType,
    val mediaFiles: List<MediaFile>
) {

    val isSet = mediaFiles.any(MediaFile::isPartOfSet)

    val size = mediaFiles.size

    override fun toString(): String {
        return "${javaClass.simpleName}(type=${type.name}, name=$name, directory=$directory, mediaFiles=<$size>)"
    }
}
