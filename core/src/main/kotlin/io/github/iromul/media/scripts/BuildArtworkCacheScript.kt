package io.github.iromul.media.scripts

import io.github.iromul.media.Config
import io.github.iromul.media.artwork.ArtworkCache
import io.github.iromul.media.artwork.Images
import io.github.iromul.media.library.MediaLibrary
import io.github.iromul.media.library.collection.MediaFile
import io.github.iromul.media.library.layout.DefaultMediaCollectionLayout
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode
import net.coobird.thumbnailator.Thumbnails
import java.io.File

class BuildArtworkCacheScript(
    mediaRoot: File,
    dryRun: Boolean = false
) : Script(mediaRoot, dryRun) {

    private var total = 0
    private var downloaded = 0
    private var resized = 0
    private var missed = 0

    companion object {
        const val artworkDirectory = "Artwork"
    }

    private val artworkRoot = File(mediaRoot, artworkDirectory)
    private val artworkCache = ArtworkCache(artworkRoot)

    override fun perform() {
        try {
            val library = MediaLibrary(mediaRoot, DefaultMediaCollectionLayout(mediaRoot))

            library.forEachCollection {
                it.forEachMediaFile { mediaFile ->
                    try {
                        val cacheEntry = artworkCache.find(mediaFile.artist, mediaFile.album)

                        processCovers(cacheEntry, mediaFile)

                        ++total
                    } catch (e: Exception) {
                        System.err.println("An error occurred during file processing: ${e.message}\n" +
                                "File: ${mediaFile.file}")

                        throw e
                    }
                }
            }
        } catch (e: Exception) {
            System.err.println("An error occurred during script execution: ${e.message}")
        }

        println("Totally processed $total files ($downloaded downloaded, $resized resized, $missed missed)")
    }

    private fun processCovers(cacheEntry: Images, mediaFile: MediaFile) {
        val downloadSize = Config.ArtworkCache.downloadSize
        val downloadImageCache = cacheEntry[downloadSize, "jpg"]

        val terms = ArtistAlbum(mediaFile.artist, mediaFile.album)

        if (downloadImageCache.isNotExists) {
            when {
                downloadImageCache.isMissed -> {
                    System.err.println("${terms.stringify()} [$downloadSize]: missed earlier")

                    ++missed
                }
                else -> {
                    val searchTerm = terms.components().joinToString(separator = " ")

                    val client = ITunesClient()

                    val image = try {
                        client.findAlbumsByTerm(searchTerm)
                            .results
                            .firstOrNull { it.matchesMediaFile(mediaFile) }
                            ?.let { entry ->
                                entry.artworkUrl100?.let {
                                    client.getArtworkSpecificSize(it, downloadSize)
                                }
                            }
                    } catch (e: ClientRequestException) {
                        throw if (e.response.status == HttpStatusCode.Forbidden) {
                            MediaFileUnprocessableException("iTunes API requests limit reached")
                        } else {
                            e
                        }
                    }

                    if (image != null && image.isNotEmpty()) {
                        downloadImageCache.write(image)

                        println("${terms.stringify()} [$downloadSize]: downloaded")

                        ++downloaded
                    } else {
                        System.err.println("${terms.stringify()} [$downloadSize]: missed")

                        downloadImageCache.markAsMissed()

                        ++missed
                    }
                }
            }
        }

        if (downloadImageCache.isExists) {
            val resizeSizes = Config.ArtworkCache.resizeSizes

            resizeSizes.forEach {
                val resizeImageCache = cacheEntry[it, "jpg"]

                val resizedFile = resizeImageCache.file

                if (!resizedFile.exists()) {
                    Thumbnails.of(downloadImageCache.file)
                        .size(500, 500)
                        .outputQuality(0.75)
                        .toFile(resizedFile)

                    println("${terms.stringify()} [$it]: resized")

                    ++resized
                }
            }
        }
    }

    private data class ArtistAlbum(
        val artist: String?,
        val album: String?
    )

    private fun ArtistAlbum.components(): List<String> {
        return listOfNotNull(artist, album)
    }

    private fun ArtistAlbum.stringify(): String {
        return components().joinToString(" - ")
    }

    private fun ITunesClient.Entry.matchesMediaFile(mediaFile: MediaFile): Boolean {
        return artistName == mediaFile.artist && collectionName == mediaFile.album
    }

    private class MediaFileUnprocessableException(message: String?) : RuntimeException(message)
}
