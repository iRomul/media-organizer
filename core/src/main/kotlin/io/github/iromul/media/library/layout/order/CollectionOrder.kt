package io.github.iromul.media.library.layout.order

import io.github.iromul.media.library.collection.MediaCollection
import io.github.iromul.media.library.collection.MediaFile

abstract class CollectionOrder(
    protected val mediaCollection: MediaCollection
) {

    abstract fun ordered(): Iterable<NamedMediaFile>
}

data class NamedMediaFile(
    val name: String,
    val mediaFile: MediaFile
)
