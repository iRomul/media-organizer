package io.github.iromul.media.commons.itunes

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.iromul.media.commons.itunes.model.Entries
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.runBlocking

class ITunesClient {

    companion object {
        private const val itunesUrl = "https://itunes.apple.com/search"
    }

    private val objectMapper = jacksonObjectMapper()
        .disable(FAIL_ON_UNKNOWN_PROPERTIES)

    private val client = HttpClient {
        install(ContentNegotiation) {
            jackson()
        }
    }

    fun findAlbumsByTerm(term: String, country: String = "ru"): Entries {
        val json = runBlocking {
            val response = client.get {
                url {
                    path(itunesUrl)

                    parameters.run {
                        append("term", term)

                        append("country", country)
                        append("entity", "album")
                    }
                }
            }

            response.body<String>()
        }

        return objectMapper.readValue(json)
    }

    fun getArtworkSpecificSize(originUrl: String, size: Int): ByteArray {
        return runBlocking {
            val response = client.get {
                url {
                    path(originUrl.replace("100x100", "${size}x${size}"))
                }
            }

            response.body()
        }
    }
}
