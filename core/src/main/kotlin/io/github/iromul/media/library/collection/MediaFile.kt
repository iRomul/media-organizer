package io.github.iromul.media.library.collection

import io.github.iromul.media.artwork.file.ImageFile
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.datatype.Artwork
import org.jaudiotagger.tag.reference.PictureTypes
import java.io.File

data class MediaFile(
    val file: File,
    val audioFile: AudioFile,
) {

    val tag: Tag
        get() = audioFile.tag

    val track: Int
        get() = tag.getFirst(FieldKey.TRACK).toIntOrNull() ?: 1
    val artist: String
        get() = tag.getFirst(FieldKey.ARTIST)
    val album: String
        get() = tag.getFirst(FieldKey.ALBUM)
    val title: String
        get() = tag.getFirst(FieldKey.TITLE)
    val diskNo: Int
        get() = tag.getFirst(FieldKey.DISC_NO).toIntOrNull() ?: 1
    val hasDisks: Boolean
        get() = tag.hasField("TPOS")
    val totalDisks: Int
        get() = tag.getFirst("TPOS").toIntOrNull() ?: 1

    val isEnumeratedTrack: Boolean
        get() = tag.hasField("TRCK")

    val isPartOfSet: Boolean
        get() = hasDisks && totalDisks > 1

    val weight: Int
        get() {
            var sum = 0

            if (isEnumeratedTrack) {
                sum += track
            }

            if (isPartOfSet) {
                sum += diskNo * 10_000
            }

            return sum
        }

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
        audioFile.commit()
    }

    private fun Artwork.isFrontCover() = pictureType == PictureTypes.DEFAULT_ID
    private fun Artwork.hasSize(size: Int) = image.raster.run { width.coerceAtLeast(height) } == size
}
