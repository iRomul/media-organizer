package io.github.iromul.media.scripts

import io.github.iromul.media.excludeRoot
import io.github.iromul.media.extension
import io.github.iromul.media.library.MediaLibraryLoader
import io.github.iromul.media.library.collection.*
import io.github.iromul.media.sanitizeWindowsFileName
import io.github.iromul.media.scripts.config.MediaLibraryLayoutConfig
import io.github.iromul.media.scripts.order.AlbumCollectionOrder
import io.github.iromul.media.scripts.order.PlaylistCollectionOrder
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class OrganizeAndCopyFilesScript(
    mediaRoot: File,
    private val output: File,
    dryRun: Boolean = false
) : Script(mediaRoot, dryRun) {

    override fun perform() {
        val mediaCollectionLayout = MediaLibraryLayoutConfig.mediaCollectionLayout

        val mediaLibrary = MediaLibraryLoader().load(mediaRoot, mediaCollectionLayout)

        mediaLibrary.mediaCollections
            .forEach(::processCollection)
    }

    private fun processCollection(collection: MediaCollection) {
        val directory = collection.directory

        val relative = Paths.get(directory.path).excludeRoot(Paths.get(mediaRoot.path))

        val targetOutput = Paths.get(output.toURI())
            .resolve(relative)
            .toFile()

        if (targetOutput.exists()) {
            println("${collection.stringify()}: Skipping because it already exists")

            return
        } else {
            fileOperation {
                targetOutput.mkdirs()
            }
        }

        println("${collection.stringify()}: Copying to ${targetOutput.path}")

        val type = collection.type

        val orderedMediaFiles = when {
            type.isAlbum -> AlbumCollectionOrder(collection)
            type.isPlaylist || type.isArtistEssentialPlaylist -> PlaylistCollectionOrder(collection)
            type.isMix -> AlbumCollectionOrder(collection)
            else -> error("Unsupported collection type: ${type.name}")
        }

        orderedMediaFiles.ordered().forEach { namedMediaFile ->
            val newName = namedMediaFile.name
            val mediaFile = namedMediaFile.mediaFile
            val sourceFile = mediaFile.file

            val sourceFileExtension = sourceFile.toPath().extension

            val targetFileName = "${newName.sanitizeWindowsFileName()}.$sourceFileExtension"
            val targetPath = Paths.get(targetOutput.path, targetFileName)
            val targetFile = targetPath.toFile()

            if (!targetFile.exists()) {
                println("\tCopying ${mediaFile.stringify()} as '$targetFileName'")

                fileOperation {
                    Files.copy(sourceFile.toPath(), targetPath)
                }
            } else {
                println("Skipping '$newName' because file is already exists")
            }
        }
    }
}
