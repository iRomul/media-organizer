package io.github.iromul.media.commons.itunes

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.iromul.media.commons.itunes.model.Entries
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
        val json = runBlocking {
            client.get<ByteArray>(url) {
                url {
                    parameters.run {
                        append("term", term)

                        append("country", country)
                        append("entity", "album")
                    }
                }
            }
        }

        return objectMapper.readValue(json, Entries::class.java)
    }

    fun getArtworkSpecificSize(originUrl: String, size: Int): ByteArray {
        return runBlocking {
            client.get<ByteArray>(originUrl.replace("100x100", "${size}x${size}"))
        }
    }
}
