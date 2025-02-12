package io.github.iromul.media.scripts.order

import io.github.iromul.media.dropExtension
import io.github.iromul.media.library.collection.MediaCollection
import io.github.iromul.media.library.layout.order.CollectionOrder
import io.github.iromul.media.library.layout.order.NamedMediaFile

class MixCollectionOrder : CollectionOrder {

    override fun ordered(mediaCollection: MediaCollection): Iterable<NamedMediaFile> {
        return mediaCollection.mediaFiles
            .map {
                NamedMediaFile(it.file.toPath().dropExtension, it)
            }
    }
}