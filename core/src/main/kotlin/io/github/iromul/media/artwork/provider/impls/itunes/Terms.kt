package io.github.iromul.media.artwork.provider.impls.itunes

import io.github.iromul.media.library.collection.MediaFile

data class Terms(
    val artist: String?,
    val album: String?
)

fun Terms.components(): List<String> {
    return listOfNotNull(artist, album)
}

fun MediaFile.toTerms() = Terms(artist, album)
