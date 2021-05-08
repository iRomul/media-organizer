package io.github.iromul.media.scripts

import io.github.iromul.media.library.MediaLibraryLoader
import io.github.iromul.media.library.collection.isMix
import io.github.iromul.media.library.collection.stringify
import io.github.iromul.media.scripts.config.Config
import io.github.iromul.media.scripts.config.MediaLibraryLayoutConfig
import java.io.File

class AssignArtworkToMediaFilesScript(
    mediaRoot: File,
    dryRun: Boolean = false
) : Script(mediaRoot, dryRun) {

    override fun perform() {
        val mediaCollectionLayout = MediaLibraryLayoutConfig.mediaCollectionLayout
        val mediaLibrary = MediaLibraryLoader().load(mediaRoot, mediaCollectionLayout)

        val provider = Config.AssignArtworkToMediaFilesScript.artworkProvider

        mediaLibrary.mediaCollections
            .filter { !it.type.isMix }
            .forEach {
                if (!it.type.isMix) {
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

                                println("${mediaFile.stringify()}: Cover of size $targetSize was added")
                            } else {
                                println("${mediaFile.stringify()}: Already has cover of size $targetSize")
                            }
                        } else {
                            System.err.println("${mediaFile.stringify()}: No cover of size $targetSize was found")
                        }
                    }
                }
            }
    }
}
