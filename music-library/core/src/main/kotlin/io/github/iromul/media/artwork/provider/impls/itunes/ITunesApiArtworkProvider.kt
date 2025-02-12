package io.github.iromul.media.artwork.provider.impls.itunes

import io.github.iromul.media.artwork.file.BinaryImageFile
import io.github.iromul.media.artwork.file.ImageFilesCollection
import io.github.iromul.media.artwork.provider.ArtworkProvider
import io.github.iromul.media.artwork.provider.MediaFileUnprocessableException
import io.github.iromul.media.commons.itunes.ITunesClient
import io.github.iromul.media.commons.itunes.model.Entry
import io.github.iromul.media.library.collection.MediaFile
import io.ktor.client.plugins.*
import io.ktor.http.*

class ITunesApiArtworkProvider(
    private val client: ITunesClient,
    private val failWhenLimitIsReached: Boolean = true,
    private val persistentState: PersistentState? = null
) : ArtworkProvider {

    companion object {
        private const val iTunesMediumImageSize = 600
    }

    private var limitReached = false

    override fun find(mediaFile: MediaFile): ImageFilesCollection {
        val terms = mediaFile.toTerms()

        if (persistentState?.shouldBeSkipped(terms) == true || limitReached) {
            return ImageFilesCollection.Empty
        }

        val image = performRequest(terms, mediaFile)

        return if (image != null) {
            val imageFile = BinaryImageFile(iTunesMediumImageSize, "image/jpeg", image)

            persistentState?.found()

            ImageFilesCollection(listOf(imageFile))
        } else {
            persistentState?.notFound(terms)

            ImageFilesCollection.Empty
        }
    }

    private fun performRequest(terms: Terms, mediaFile: MediaFile): ByteArray? {
        return try {
            val searchTerm = terms.components().joinToString(separator = " ")

            client.findAlbumsByTerm(searchTerm)
                .results
                .firstOrNull { it.matchesMediaFile(mediaFile) }
                ?.let { entry ->
                    entry.artworkUrl100?.let {
                        client.getArtworkSpecificSize(it, iTunesMediumImageSize)
                    }
                }
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Forbidden) {
                if (failWhenLimitIsReached) {
                    throw MediaFileUnprocessableException("iTunes API requests limit reached")
                } else {
                    limitReached = true

                    null
                }
            } else {
                throw e
            }
        }
    }

    private fun Entry.matchesMediaFile(mediaFile: MediaFile): Boolean {
        return artistName == mediaFile.artist && collectionName == mediaFile.album
    }
}
