package io.github.iromul.media.artwork.provider

import io.github.iromul.media.artwork.file.ImageFilesCollection
import io.github.iromul.media.library.collection.MediaFile

interface ArtworkStore : ArtworkProvider {

    fun add(mediaFile: MediaFile, collection: ImageFilesCollection)
}
