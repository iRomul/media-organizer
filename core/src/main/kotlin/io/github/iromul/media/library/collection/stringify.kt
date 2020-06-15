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

fun MediaFile.stringify(includeTrack: Boolean = false, includeFile: Boolean = false): String {
    val minimal = listOf(artist, album, title).joinToString(separator = " - ") { it.notNullOrBlankOrElse("<N/A>") }

    var result = minimal

    if (includeTrack) {
        result = "$track. $result"
    }

    if (includeFile) {
        result += " (file $file)"
    }

    return result
}

private fun String?.notNullOrBlankOrElse(default: String) = if (isNullOrBlank()) default else this!!
