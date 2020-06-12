package io.github.iromul.media.library.collection

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

class MediaCollection(
    val name: String,
    val directory: File,
    val type: MediaCollectionType,
    files: List<File>
) : Collection<MediaFile> {

    private val mediaFiles = files.map {
        val audioFile = AudioFileIO.read(it)

        val tag = audioFile.tag

        val track = tag.getFirst(FieldKey.TRACK).toInt()
        val artist = tag.getFirst(FieldKey.ARTIST)
        val album = tag.getFirst(FieldKey.ALBUM)
        val title = tag.getFirst(FieldKey.TITLE)

        MediaFile(it, audioFile, tag, track, artist, album, title)
    }

    fun forEachMediaFile(action: (MediaFile) -> Unit) = forEach {
        action(it)

        if (it.shouldBeCommitted) {
            it.audioFile.commit()
        }
    }

    override val size = mediaFiles.size

    override fun contains(element: MediaFile) = mediaFiles.contains(element)

    override fun containsAll(elements: Collection<MediaFile>) = mediaFiles.containsAll(elements)

    override fun isEmpty() = mediaFiles.isEmpty()

    override fun iterator() = mediaFiles.iterator()
}
