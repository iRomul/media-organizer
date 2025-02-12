package io.github.iromul.media.scripts

import io.github.iromul.media.library.MediaLibraryLoader
import io.github.iromul.media.library.collection.isMix
import io.github.iromul.media.library.collection.stringify
import io.github.iromul.media.scripts.config.Config
import io.github.iromul.media.scripts.config.MediaLibraryLayoutConfig
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import java.io.File

inline fun MeterRegistry.withTimer(
    name: String,
    tags: Iterable<Tag> = Tags.empty(),
    crossinline thunk: () -> Unit
) {
    timer(name, tags).record(Runnable { thunk() })
}

class AssignArtworkToMediaFilesScript(
    mediaRoot: File,
    dryRun: Boolean = false
) : Script(mediaRoot, dryRun) {

    override fun perform() {
        val mediaCollectionLayout = MediaLibraryLayoutConfig.mediaCollectionLayout
        val mediaLibrary = MediaLibraryLoader().load(mediaRoot, mediaCollectionLayout)

        val provider = Config.AssignArtworkToMediaFilesScript.artworkProvider

        val registry = SimpleMeterRegistry()

        val total = registry.counter("assign.artwork.total")
        val success = registry.counter("assign.artwork.success")
        val skipped = registry.counter("assign.artwork.skipped")
        val failed = registry.counter("assign.artwork.failed")

        mediaLibrary.mediaCollections
            .filter { !it.type.isMix }
            .forEach {
                it.mediaFiles.forEach { mediaFile ->
                    val targetSize = Config.AssignArtworkToMediaFilesScript.targetSize

                    val imageFilesCollection = provider.find(mediaFile)
                    val imageFile = imageFilesCollection.findImageFileByMimeAndSize("image/jpeg", targetSize)

                    if (imageFile != null) {
                        if (!mediaFile.hasFrontCoverOfSize(targetSize)) {
                            fileOperation {
                                mediaFile.addArtwork(imageFile)

                                mediaFile.save()
                            }

                            success.increment()

                            println("${mediaFile.stringify()}: Cover of size $targetSize was added")
                        } else {
                            skipped.increment()

                            println("${mediaFile.stringify()}: Already has cover of size $targetSize")
                        }
                    } else {
                        failed.increment()

                        System.err.println("${mediaFile.stringify()}: No cover of size $targetSize was found")
                    }

                    total.increment()
                }
            }

        println(buildString {
            append("Success ${success.count().toInt()} of ${total.count().toInt()}")

            if (skipped.count().toInt() != 0) {
                append(", ${skipped.count().toInt()} skipped")
            }

            if (failed.count().toInt() != 0) {
                append(", ${failed.count().toInt()} failed")
            }
        })
    }
}
