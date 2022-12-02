package io.github.iromul.media.scripts.order

import io.github.iromul.media.library.collection.MediaCollection
import io.github.iromul.media.library.collection.MediaFile
import io.github.iromul.media.library.layout.order.CollectionOrder
import io.github.iromul.media.library.layout.order.NamedMediaFile
import io.github.iromul.media.meta.loadPlaylistData
import io.github.iromul.media.playlist.PlaylistData
import java.io.File

class PlaylistCollectionOrder : CollectionOrder {

    override fun ordered(mediaCollection: MediaCollection): Iterable<NamedMediaFile> {
        val totalTracks = mediaCollection.size
        val totalTracksDigits = totalTracks.toString().length

        val playlistFile = File(mediaCollection.directory, "playlist.txt")

        val orderedMediaFiles = if (playlistFile.exists() && playlistFile.isFile) {
            val playlist = loadPlaylistData(playlistFile)

            mediaCollection.mediaFiles.reorderByPlaylist(playlist)
        } else {
            mediaCollection.mediaFiles.shuffled()
        }

        return orderedMediaFiles.mapIndexed { index, mediaFile ->
            val filePositionFormatted = (index + 1).toString().padStart(totalTracksDigits, '0')
            val fileName = "$filePositionFormatted - ${mediaFile.artist} - ${mediaFile.title}"

            NamedMediaFile(fileName, mediaFile)
        }
    }

    private fun Iterable<MediaFile>.reorderByPlaylist(playlist: PlaylistData): List<MediaFile> {
        return playlist.map { playlistEntry ->
            val collectionFile = find { it.title == playlistEntry.title }
                ?: throw IllegalStateException("Playlist entry ${playlistEntry.title} is not found in collection")

            collectionFile
        }
    }
}
