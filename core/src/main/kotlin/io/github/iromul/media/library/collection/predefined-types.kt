package io.github.iromul.media.library.collection

val MediaCollectionType.isPlaylist: Boolean
    get() = name == "Playlist"

val MediaCollectionType.isAlbum: Boolean
    get() = name == "Album"

val MediaCollectionType.isArtistEssentialPlaylist: Boolean
    get() = name == "Artist Essential Playlist"

val MediaCollectionType.isMix: Boolean
    get() = name == "Mix"