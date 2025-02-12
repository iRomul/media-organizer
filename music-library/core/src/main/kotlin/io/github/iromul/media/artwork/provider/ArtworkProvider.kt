package io.github.iromul.media.artwork.provider

import io.github.iromul.media.artwork.file.ImageFilesCollection
import io.github.iromul.media.library.collection.MediaFile

interface ArtworkProvider {

    fun find(mediaFile: MediaFile): ImageFilesCollection
}
