package io.github.iromul.media.library.layout.order

import io.github.iromul.media.library.collection.MediaCollection
import io.github.iromul.media.library.collection.MediaFile

interface CollectionOrder {

    fun ordered(mediaCollection: MediaCollection): Iterable<NamedMediaFile>
}

data class NamedMediaFile(
    val name: String,
    val mediaFile: MediaFile
)
