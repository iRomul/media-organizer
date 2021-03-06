package io.github.iromul.media.scripts

import io.github.iromul.media.scripts.config.Config
import io.github.iromul.media.library.MediaLibrary
import io.github.iromul.media.library.collection.stringify
import io.github.iromul.media.library.layout.DefaultMediaCollectionLayout
import java.io.File

class BuildArtworkCacheScript(
    mediaRoot: File,
    dryRun: Boolean = false
) : Script(mediaRoot, dryRun) {

    override fun perform() {
        val provider = Config.BuildArtworkCacheScript.artworkProvider

        try {
            val library = MediaLibrary(mediaRoot, DefaultMediaCollectionLayout(mediaRoot))

            library.forEachCollection {
                it.mediaFiles.forEach { mediaFile ->
                    try {
                        val cacheEntry = provider.find(mediaFile)

                        print(mediaFile.stringify())

                        if (cacheEntry.isEmpty()) {
                            println(": Not loaded")
                        } else {
                            println(": ${cacheEntry.joinToString { e -> e.size.toString() }}")
                        }
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
        }
    }
}
