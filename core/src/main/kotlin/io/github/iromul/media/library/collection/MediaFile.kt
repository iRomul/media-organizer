package io.github.iromul.media.library.collection

import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.datatype.Artwork
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

    fun hasArtworks(): Boolean {
        return tag.artworkList.isNotEmpty()
    }

    fun addArtwork(artwork: Artwork) {
        tag.setField(artwork)
    }

    fun save() {
        shouldBeCommitted = true
    }
}
