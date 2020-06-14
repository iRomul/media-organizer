package io.github.iromul.media.scripts

import io.github.iromul.media.artwork.provider.impls.AutoCacheableArtworkStore
import io.github.iromul.media.artwork.provider.impls.AutoResizerArtworkStoreProxy
import io.github.iromul.media.artwork.provider.impls.FileCacheArtworkStore
import io.github.iromul.media.artwork.provider.impls.MediaFileReaderArtworkProvider
import io.github.iromul.media.artwork.provider.impls.itunes.ITunesApiArtworkProvider
import io.github.iromul.media.artwork.provider.impls.itunes.PersistentState
import io.github.iromul.media.commons.fman.FileStorageManager
import io.github.iromul.media.commons.itunes.ITunesClient
import io.github.iromul.media.library.MediaLibrary
import io.github.iromul.media.library.collection.MediaFile
import io.github.iromul.media.library.layout.DefaultMediaCollectionLayout
import java.io.File

class BuildArtworkCacheScript(
    mediaRoot: File,
    dryRun: Boolean = false
) : Script(mediaRoot, dryRun) {

    private val storePath = File(mediaRoot, ".store")

    private val itunesProviderCachePath = File(storePath, "itunes")
    private val fileCachePath = File(storePath, "imgcache")

    override fun perform() {
        val iTunesClient = ITunesClient()
        val onlineProvider = ITunesApiArtworkProvider(
            iTunesClient,
            failWhenLimitIsReached = true,
            persistentState = PersistentState(itunesProviderCachePath)
        )

        val mediaFileProvider = MediaFileReaderArtworkProvider()

        val fileCacheStore = AutoResizerArtworkStoreProxy(
            FileCacheArtworkStore(FileStorageManager(fileCachePath)),
            listOf(500)
        )

        val provider = AutoCacheableArtworkStore(
            listOf(mediaFileProvider, onlineProvider),
            fileCacheStore
        )

        try {
            val library = MediaLibrary(mediaRoot, DefaultMediaCollectionLayout(mediaRoot))

            library.forEachCollection {
                it.forEachMediaFile { mediaFile ->
                    try {
                        val cacheEntry = provider.find(mediaFile)

                        print(mediaFile.stringifyShort())

                        if (cacheEntry.isEmpty()) {
                            println(": Not loaded")
                        } else {
                            println(": ${cacheEntry.joinToString { e -> e.size.toString() } }")
                        }
                    } catch (e: Exception) {
                        System.err.println(
                            "An error occurred during file processing: ${e.message}\n" +
                                    "File: ${mediaFile.file}"
                        )

                        throw e
                    }
                }
            }
        } catch (e: Exception) {
            System.err.println("An error occurred during script execution: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun MediaFile.components(): List<String> {
        return listOfNotNull(artist, album)
    }

    private fun MediaFile.stringifyShort(): String {
        return components().joinToString(" - ")
    }
}
