package io.github.iromul.media.library.layout

import io.github.iromul.media.excludeRoot
import io.github.iromul.media.library.collection.MediaCollectionType
import java.io.File
import java.nio.file.Path

class DefaultMediaCollectionLayout(
    private val mediaRoot: File
) : MediaCollectionLayout {

    companion object {
        private const val albumsRoot = "Artists"
        private const val playlistsRoot = "Playlists"
        private const val essentialSign = "# "
    }

    override fun collectionType(file: File): MediaCollectionType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isPlaylist(file: File) = file.mediaRootRelativePath.startsWith(playlistsRoot)

    override fun isAlbum(file: File) = file.mediaRootRelativePath.run {
        startsWith(albumsRoot) && !last().toString().startsWith(essentialSign)
    }

    override fun isArtistEssential(file: File) = file.mediaRootRelativePath.run {
        startsWith(albumsRoot) && last().toString().startsWith(essentialSign)
    }

    private val File.mediaRootRelativePath: Path
        get() = toPath().excludeRoot(mediaRoot.toPath())
}
