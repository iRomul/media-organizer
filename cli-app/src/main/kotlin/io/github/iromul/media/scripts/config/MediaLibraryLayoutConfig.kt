package io.github.iromul.media.scripts.config

import io.github.iromul.media.library.layout.MediaCollectionLayout
import io.github.iromul.media.scripts.order.AlbumCollectionOrder
import io.github.iromul.media.scripts.order.MixCollectionOrder
import io.github.iromul.media.scripts.order.PlaylistCollectionOrder
import java.io.File

object MediaLibraryLayoutConfig {

    private val defaultFileMatcher = { mediaFile: File ->
        mediaFile.extension in listOf("mp3", "aac", "m4a")
    }

    private val mediaCollectionTypes = listOf(
        registerMediaCollectionType(
            name = "Playlist",
            matcher = byRelativePath {
                startsWith("Playlists") && nestingLevel() in (1..2)
            },
            mediaFilesMatcher = defaultFileMatcher,
            order = PlaylistCollectionOrder()
        ),
        registerMediaCollectionType(
            name = "Album",
            matcher = byRelativePath {
                startsWith("Artists") && nestingLevel() == 2 && !endsWith { it.startsWith("#") }
            },
            mediaFilesMatcher = defaultFileMatcher,
            order = AlbumCollectionOrder()
        ),
        registerMediaCollectionType(
            name = "Artist Essential Playlist",
            matcher = byRelativePath {
                startsWith("Artists") && nestingLevel() == 2 && endsWith { it.startsWith("#") }
            },
            mediaFilesMatcher = defaultFileMatcher,
            order = PlaylistCollectionOrder()
        ),
        registerMediaCollectionType(
            name = "OST",
            matcher = byRelativePath {
                startsWith("OST") && nestingLevel() in (1..2)
            },
            mediaFilesMatcher = defaultFileMatcher,
            order = PlaylistCollectionOrder()
        ),
        registerMediaCollectionType(
            name = "Mix",
            matcher = byRelativePath {
                startsWith("Mixes") && nestingLevel() == 1
            },
            mediaFilesMatcher = defaultFileMatcher,
            order = MixCollectionOrder()
        )
    )

    val mediaCollectionLayout = MediaCollectionLayout(
        Config.mediaRoot,
        mediaCollectionTypes
    )
}