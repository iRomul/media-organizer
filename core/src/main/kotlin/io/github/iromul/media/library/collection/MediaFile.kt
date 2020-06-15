package io.github.iromul.media.library.collection

import io.github.iromul.media.artwork.file.ImageFile
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.datatype.Artwork
import org.jaudiotagger.tag.reference.PictureTypes
import java.io.File

data class MediaFile(
    val file: File,
    val audioFile: AudioFile,
    val tag: Tag,

    val track: Int,
    val artist: String,
    val album: String,
    val title: String
) {

    var shouldBeCommitted = false

    fun hasFrontCoverOfSize(size: Int) = tag.artworkList.any { it.isFrontCover() && it.hasSize(size) }

    fun addArtwork(imageFile: ImageFile) {
        val artwork = Artwork().apply {
            mimeType = imageFile.mime
            binaryData = imageFile.bytes()
            pictureType = PictureTypes.DEFAULT_ID
        }

        addArtwork(artwork)
    }

    fun addArtwork(artwork: Artwork) {
        tag.setField(artwork)
    }

    fun save() {
        shouldBeCommitted = true
    }

    private fun Artwork.isFrontCover() = pictureType == PictureTypes.DEFAULT_ID
    private fun Artwork.hasSize(size: Int) = image.raster.run { width.coerceAtLeast(height) } == size
}
