package io.github.iromul.media.library.layout

import io.github.iromul.media.library.collection.MediaCollectionType
import java.io.File

interface MediaCollectionLayout {

    fun collectionType(file: File): MediaCollectionType

    fun isPlaylist(file: File): Boolean
    fun isAlbum(file: File): Boolean
    fun isArtistEssential(file: File): Boolean
}
