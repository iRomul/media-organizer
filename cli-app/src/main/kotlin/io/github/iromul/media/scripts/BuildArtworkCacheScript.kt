package io.github.iromul.media.scripts

import io.github.iromul.media.library.MediaLibraryLoader
import io.github.iromul.media.library.collection.isMix
import io.github.iromul.media.library.collection.stringify
import io.github.iromul.media.scripts.config.Config
import io.github.iromul.media.scripts.config.MediaLibraryLayoutConfig
import java.io.File

class BuildArtworkCacheScript(
    mediaRoot: File,
    dryRun: Boolean = false
) : Script(mediaRoot, dryRun) {

    override fun perform() {
        val mediaCollectionLayout = MediaLibraryLayoutConfig.mediaCollectionLayout
        val mediaLibrary = MediaLibraryLoader().load(mediaRoot, mediaCollectionLayout)

        val provider = Config.BuildArtworkCacheScript.artworkProvider

        var total = 0
        var success = 0

        try {
            mediaLibrary.mediaCollections
                .filter { !it.type.isMix }
                .forEach {
                    it.mediaFiles.forEach { mediaFile ->
                        try {
                            val cacheEntry = provider.find(mediaFile)

                            print(mediaFile.stringify())

                            if (cacheEntry.isEmpty()) {
                                println(": Not loaded")
                            } else {
                                ++success

                                println(": ${cacheEntry.joinToString { e -> e.size.toString() }}")
                            }

                            ++total
                        } catch (e: Exception) {
                            System.err.println(
                                "An error occurred during file processing: ${e.message}\n" +
                                        "File: ${mediaFile.file}"
                            )

                            throw e
                        }
                    }
                }
        } catch (e: Exception) {
            System.err.println("An error occurred during script execution: ${e.message}")
            e.printStackTrace()
        } finally {
            println("Success $success of $total")
        }
    }
}
