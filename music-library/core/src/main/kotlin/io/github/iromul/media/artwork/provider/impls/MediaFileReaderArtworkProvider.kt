package io.github.iromul.media.artwork.provider.impls

import io.github.iromul.media.artwork.file.BinaryImageFile
import io.github.iromul.media.artwork.file.ImageFile
import io.github.iromul.media.artwork.file.ImageFilesCollection
import io.github.iromul.media.artwork.provider.ArtworkProvider
import io.github.iromul.media.library.collection.MediaFile
import org.jaudiotagger.tag.datatype.Artwork

class MediaFileReaderArtworkProvider : ArtworkProvider {

    override fun find(mediaFile: MediaFile): ImageFilesCollection {
        return mediaFile.tag.artworkList
            .filter { it.mimeType == "image/jpeg" }
            .map { it.toImageFile() }
            .let(::ImageFilesCollection)
    }

    private fun Artwork.toImageFile(): ImageFile {
        val (width, height) = image.raster.run { width to height }

        val size = width.coerceAtLeast(height)

        return BinaryImageFile(size, "image/jpeg", binaryData)
    }
}
