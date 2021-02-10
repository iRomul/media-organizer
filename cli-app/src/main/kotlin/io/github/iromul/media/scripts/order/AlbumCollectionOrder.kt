package io.github.iromul.media.scripts.order

import io.github.iromul.media.library.collection.MediaCollection
import io.github.iromul.media.library.collection.MediaFile

class AlbumCollectionOrder(
    mediaCollection: MediaCollection
) : CollectionOrder(mediaCollection) {

    override fun ordered(): Iterable<NamedMediaFile> {
        val totalTracks = mediaCollection.size
        val totalTracksDigits = totalTracks.toString().length

        return mediaCollection.mediaFiles
            .sortedBy(MediaFile::track)
            .map {
                val trackNumberFormatted = it.track.toString().padStart(totalTracksDigits, '0')
                val fileName = "$trackNumberFormatted - ${it.title}"

                NamedMediaFile(fileName, it)
            }
    }
}
