package io.github.iromul.media.scripts.order

import io.github.iromul.media.library.collection.MediaCollection
import io.github.iromul.media.library.collection.MediaFile
import io.github.iromul.media.library.layout.order.CollectionOrder
import io.github.iromul.media.library.layout.order.NamedMediaFile

class AlbumCollectionOrder : CollectionOrder {

    override fun ordered(mediaCollection: MediaCollection): Iterable<NamedMediaFile> {
        val totalTracks = mediaCollection.size
        val totalTracksDigits = totalTracks.toString().length.coerceAtLeast(2)

        return mediaCollection.mediaFiles
            .sortedBy(MediaFile::weight)
            .map {
                val trackNumberFormatted = if (it.isEnumeratedTrack) {
                    it.track.toString().padStart(totalTracksDigits, '0')
                } else {
                    null
                }

                val diskNoFormatted = if (mediaCollection.isSet) {
                    it.diskNo.toString().padStart(2, '0')
                } else {
                    null
                }

                val positionFormatted = listOfNotNull(diskNoFormatted, trackNumberFormatted)
                    .ifEmpty { null }
                    ?.joinToString("-")

                val fileName = listOfNotNull(positionFormatted, it.title)
                    .joinToString(" - ")

                NamedMediaFile(fileName, it)
            }
    }
}
