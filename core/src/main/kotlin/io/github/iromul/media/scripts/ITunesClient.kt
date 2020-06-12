package io.github.iromul.media.scripts

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking

class ITunesClient {

    companion object {
        private const val url = "https://itunes.apple.com/search"
    }

    private val objectMapper = jacksonObjectMapper()
        .disable(FAIL_ON_UNKNOWN_PROPERTIES)

    private val client = HttpClient {
        install(JsonFeature)
    }

    fun findAlbumsByTerm(term: String, country: String = "ru"): Entries {
        return runBlocking {
            val json = client.get<ByteArray>(url) {
                url {
                    parameters.run {
                        append("term", term)

                        append("country", country)
                        append("entity", "album")
                    }
                }
            }

            objectMapper.readValue(json, Entries::class.java)
        }
    }

    fun getArtworkSpecificSize(originUrl: String, size: Int): ByteArray {
        return runBlocking {
            client.get<ByteArray>(originUrl.replace("100x100", "${size}x${size}"))
        }
    }

    data class Entries(
        var resultCount: Int? = null,
        var results: List<Entry> = emptyList()
    ) : Iterable<Entry> {
        override fun iterator() = results.iterator()
    }

    class Entry(
        var artistName: String? = null,
        var collectionName: String? = null,
        var artworkUrl60: String? = null,
        var artworkUrl100: String? = null
    )
}
