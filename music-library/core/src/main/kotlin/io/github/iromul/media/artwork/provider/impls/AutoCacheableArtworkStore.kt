package io.github.iromul.media.artwork.provider.impls

import io.github.iromul.media.artwork.file.ImageFilesCollection
import io.github.iromul.media.artwork.provider.ArtworkProvider
import io.github.iromul.media.artwork.provider.ArtworkStore
import io.github.iromul.media.library.collection.MediaFile

class AutoCacheableArtworkStore(
    private val providers: Iterable<ArtworkProvider>,
    private val store: ArtworkStore
) : ArtworkStore {

    override fun add(mediaFile: MediaFile, collection: ImageFilesCollection) {
        store.add(mediaFile, collection)
    }

    override fun find(mediaFile: MediaFile): ImageFilesCollection {
        val entry = store.find(mediaFile)

        if (entry.isNotEmpty()) {
            return entry
        }

        val result = providers.asSequence()
            .map { provider -> provider.find(mediaFile) }
            .firstOrNull { foundEntry -> foundEntry.isNotEmpty() }
            ?: return ImageFilesCollection.Empty

        store.add(mediaFile, result)

        return result
    }
}
