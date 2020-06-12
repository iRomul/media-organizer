package io.github.iromul.media.library.collection

import io.github.iromul.media.library.collection.MediaCollectionType.*

fun MediaCollection.stringify(): String {
    val typeString = when (type) {
        ALBUM -> "album"
        PLAYLIST -> "playlist"
        ARTIST_ESSENTIAL_PLAYLIST -> "playlist (artist essential)"
    }

    return "$typeString $name"
}

fun MediaFile.stringify() = "$artist - $album - $title (track $track)"
