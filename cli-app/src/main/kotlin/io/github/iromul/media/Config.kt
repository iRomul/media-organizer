package io.github.iromul.media

import io.github.iromul.media.artwork.provider.impls.AutoCacheableArtworkStore
import io.github.iromul.media.artwork.provider.impls.AutoResizerArtworkStoreProxy
import io.github.iromul.media.artwork.provider.impls.FileCacheArtworkStore
import io.github.iromul.media.artwork.provider.impls.MediaFileReaderArtworkProvider
import io.github.iromul.media.artwork.provider.impls.itunes.ITunesApiArtworkProvider
import io.github.iromul.media.artwork.provider.impls.itunes.PersistentState
import io.github.iromul.media.commons.fman.FileStorageManager
import io.github.iromul.media.commons.itunes.ITunesClient
import java.io.File
import java.util.logging.LogManager

object Config {

    lateinit var mediaRoot: File

    object Globals {

        private val storePath = File(mediaRoot, ".store")

        val itunesProviderCachePath = File(storePath, "itunes")
        val fileCachePath = File(storePath, "imgcache")
    }

    fun logging() {
        LogManager.getLogManager().reset()
    }

    object AssignArtworkToMediaFilesScript {
        const val targetSize = 500

        val artworkProvider = FileCacheArtworkStore(FileStorageManager(Globals.fileCachePath))
    }

    object BuildArtworkCacheScript {

        private val iTunesClient = ITunesClient()
        private val onlineProvider = ITunesApiArtworkProvider(
            iTunesClient,
            failWhenLimitIsReached = true,
            persistentState = PersistentState(Globals.itunesProviderCachePath)
        )

        private val mediaFileProvider = MediaFileReaderArtworkProvider()

        private val fileCacheStore = AutoResizerArtworkStoreProxy(
            FileCacheArtworkStore(FileStorageManager(Globals.fileCachePath)),
            listOf(500)
        )

        val artworkProvider = AutoCacheableArtworkStore(
            listOf(mediaFileProvider, onlineProvider),
            fileCacheStore
        )
    }
}
