package io.github.iromul.media.scripts.config

import io.github.iromul.media.library.layout.MediaCollectionLayout
import io.github.iromul.media.library.layout.MediaCollectionTypeHandler
import java.io.File
import java.nio.file.Path

object MediaLibraryLayoutConfig {

    private val defaultFileMatcher = { mediaFile: File ->
        mediaFile.extension in listOf("mp3", "aac", "m4a")
    }

    private val mediaCollectionTypes = listOf(
        MediaCollectionTypeHandler(
            name = "Playlist",
            matcher = byRelativePath {
                startsWith("Playlists") && nestingLevel() in (1..2)
            },
            mediaFilesMatcher = defaultFileMatcher
        ),
        MediaCollectionTypeHandler(
            name = "Album",
            matcher = byRelativePath {
                startsWith("Artists") && nestingLevel() == 2 && !endsWith { it.startsWith("#") }
            },
            mediaFilesMatcher = defaultFileMatcher
        ),
        MediaCollectionTypeHandler(
            name = "Artist Essential Playlist",
            matcher = byRelativePath {
                startsWith("Artists") && nestingLevel() == 2 && endsWith { it.startsWith("#") }
            },
            mediaFilesMatcher = defaultFileMatcher
        ),
        MediaCollectionTypeHandler(
            name = "Mix",
            matcher = byRelativePath {
                startsWith("Mixes") && nestingLevel() == 1
            },
            mediaFilesMatcher = defaultFileMatcher
        )
    )

    val mediaCollectionLayout = MediaCollectionLayout(
        Config.mediaRoot,
        mediaCollectionTypes
    )

    private fun byRelativePath(matcher: Path.() -> Boolean) =
        { _: File, relativePath: Path -> relativePath.matcher() }

    private fun Path.nestingLevel() = nameCount - 1

    private fun Path.endsWith(matcher: (String) -> Boolean): Boolean {
        return matcher(last().toString())
    }
}