package io.github.iromul.media.artwork.provider.impls

import io.github.iromul.media.artwork.file.BinaryImageFile
import io.github.iromul.media.artwork.file.ImageFilesCollection
import io.github.iromul.media.artwork.provider.ArtworkStore
import io.github.iromul.media.library.collection.MediaFile
import net.coobird.thumbnailator.Thumbnails
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class AutoResizerArtworkStoreProxy(
    private val artworkStore: ArtworkStore,
    private val targetSizes: List<Int>
) : ArtworkStore by artworkStore {

    override fun add(mediaFile: MediaFile, collection: ImageFilesCollection) {
        if (collection.isEmpty()) {
            return artworkStore.add(mediaFile, collection)
        }

        val jpegs = collection.filter { it.mime == "image/jpeg" }
        val storedSizes = jpegs.map { it.size }

        val remainsTargets = targetSizes.filter { it !in storedSizes }
        val largestImage = collection.maxByOrNull { it.size }!!

        val bais = ByteArrayInputStream(largestImage.bytes())

        remainsTargets.forEach { targetSize ->
            val baos = ByteArrayOutputStream()

            Thumbnails.of(bais)
                .size(targetSize, targetSize)
                .outputQuality(0.75)
                .toOutputStream(baos)

            collection.add(BinaryImageFile(targetSize, "image/jpeg", baos.toByteArray()))

            println("[DEBUG] Resized to $targetSize")
        }

        artworkStore.add(mediaFile, collection)
    }
}
