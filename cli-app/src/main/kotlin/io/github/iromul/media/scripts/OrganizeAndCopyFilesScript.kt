package io.github.iromul.media.scripts

import io.github.iromul.media.excludeRoot
import io.github.iromul.media.extension
import io.github.iromul.media.library.MediaLibrary
import io.github.iromul.media.library.collection.MediaCollection
import io.github.iromul.media.library.collection.MediaCollectionType.*
import io.github.iromul.media.library.collection.stringify
import io.github.iromul.media.library.layout.DefaultMediaCollectionLayout
import io.github.iromul.media.sanitizeWindowsFileName
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

    private val library = MediaLibrary(mediaRoot, DefaultMediaCollectionLayout(mediaRoot))

    override fun perform() {
        library.forEachCollection { collection ->
            processCollection(collection)
        }
    }

    private fun processCollection(collection: MediaCollection) {
        val directory = collection.directory

        val relative = Paths.get(directory.path).excludeRoot(Paths.get(mediaRoot.path))

        val targetOutput = Paths.get(output.toURI())
            .resolve(relative)
            .toFile()

        if (targetOutput.exists()) {
            println("Skipping ${collection.stringify()} because it already exists")

            return
        } else {
            fileOperation {
                targetOutput.mkdirs()
            }
        }

        println("Copying ${collection.stringify()} to '${targetOutput.path}'...")

        val orderedMediaFiles = when (collection.type) {
            ALBUM -> AlbumCollectionOrder(collection)
            PLAYLIST, ARTIST_ESSENTIAL_PLAYLIST -> PlaylistCollectionOrder(collection)
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
