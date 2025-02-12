package io.github.iromul.media.artwork.provider.impls

import io.github.iromul.media.artwork.file.ImageFilesCollection
import io.github.iromul.media.artwork.provider.ArtworkProvider
import io.github.iromul.media.artwork.provider.ArtworkStore
import io.github.iromul.media.library.collection.MediaFile
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class AutoCacheableArtworkStoreTest {

    @Test
    fun `auto cacheable should return value from store if present (empty providers)`() {
        val store = mockk<ArtworkStore>(relaxed = true)
        val storeFile = mockk<MediaFile>()

        val autoCacheable = AutoCacheableArtworkStore(emptyList(), store)

        autoCacheable.find(storeFile)

        verify(exactly = 1) { store.find(storeFile) }

        confirmVerified(store)
    }

    @Test
    fun `auto cacheable should return value from store if present (one providers)`() {
        val store = mockk<ArtworkStore>(relaxed = true)
        val storeFile = mockk<MediaFile>()

        val provider1 = mockk<ArtworkProvider>()

        every { provider1.find(storeFile) } returns ImageFilesCollection()

        val autoCacheable = AutoCacheableArtworkStore(listOf(provider1), store)

        autoCacheable.find(storeFile)

        verify(exactly = 0) { provider1.find(storeFile) }
        verify(exactly = 1) { store.find(storeFile) }

        confirmVerified(provider1)
        confirmVerified(store)
    }
}
