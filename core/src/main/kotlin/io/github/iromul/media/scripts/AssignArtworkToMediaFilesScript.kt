package io.github.iromul.media.scripts

import io.github.iromul.media.Config
import io.github.iromul.media.artwork.ArtworkCache
import io.github.iromul.media.library.MediaLibrary
import io.github.iromul.media.library.collection.MediaFile
import io.github.iromul.media.library.layout.DefaultMediaCollectionLayout
import org.jaudiotagger.tag.datatype.Artwork
import org.jaudiotagger.tag.reference.PictureTypes
import java.io.File

class AssignArtworkToMediaFilesScript(
    mediaRoot: File,
    dryRun: Boolean = false
) : Script(mediaRoot, dryRun) {

    private val artworkRoot = File(mediaRoot, BuildArtworkCacheScript.artworkDirectory)
    private val artworkCache = ArtworkCache(artworkRoot)

    private val library = MediaLibrary(mediaRoot, DefaultMediaCollectionLayout(mediaRoot))

    override fun perform() {
        library.forEachCollection {
            it.forEachMediaFile { mediaFile ->
                val size = Config.AssignCoversToMediaFilesScript.artworkSize

                val cacheEntry = artworkCache.find(mediaFile.artist, mediaFile.album)
                val cacheImage = cacheEntry[size, "jpg"]

                if (cacheImage.isExists) {
                    if (!mediaFile.hasArtworks()) {
                        val imageData = cacheImage.read()

                        val artwork = Artwork().apply {
                            binaryData = imageData
                            mimeType = "image/jpeg"
                            pictureType = PictureTypes.DEFAULT_ID
                        }

                        fileOperation {
                            mediaFile.addArtwork(artwork)

                            mediaFile.save()
                        }

                        println("${mediaFile.stringify()}: Cover of size $size was added")
                    }
                } else {
                    System.err.println("${mediaFile.stringify()}: No cover of size $size was found in cache")
                }
            }
        }
    }

    private fun MediaFile.stringify() = "$artist - $album - $title"
}
