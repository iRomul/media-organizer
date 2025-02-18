package io.github.iromul.home.photolib.exif.googlephoto

import io.github.iromul.home.photolib.meta.exif.googlephoto.CreationTimeModel
import io.github.iromul.home.photolib.meta.exif.googlephoto.MetadataModel
import io.github.iromul.home.photolib.meta.exif.googlephoto.PhotoTakenTimeModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.time.Instant
import java.time.ZoneOffset

class MetadataModelTest {

    @Test
    fun `bubba bibba`() {
        val json = Json.encodeToString(
            MetadataModel(
                CreationTimeModel(
                    timestamp = 123
                ),
                PhotoTakenTimeModel(
                    timestamp = 123
                ),
                JsonObject(emptyMap())
            )
        )

        println(json)
    }

    @Test
    fun `meow meow`() {
        val jsonString = readResource("20181005_191826.jpg.json")

        val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

        val model = json.decodeFromString<MetadataModel>(jsonString)

        assertAll(
            "Actual model: $model",
            { assertEquals(1538761824L, model.creationTime.timestamp) },
            { assertEquals(1538756306L, model.photoTakenTime.timestamp) },
            { assertTrue(model.googlePhotosOrigin!!.contains("mobileUpload")) }
        )
    }

    @Test
    fun `woof woof`() {
        val jsonString = readResource("1385544315864.jpg.json")

        val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

        val model = json.decodeFromString<MetadataModel>(jsonString)

        assertAll(
            "Actual model: $model",
            { assertEquals(1682288302L, model.creationTime.timestamp) },
            { assertEquals(1386145502L, model.photoTakenTime.timestamp) },
            { assertTrue(model.googlePhotosOrigin!!.contains("fromPartnerSharing")) }
        )

        val instant = Instant.ofEpochSecond(model.photoTakenTime.timestamp)

        println(instant)
        println(instant.atOffset(ZoneOffset.UTC).toLocalDateTime())
    }

    private fun Any.readResource(name: String): String {
        return requireNotNull(this::class.java.classLoader.getResource(name)) {
            "Resource $name not found"
        }.readText()
    }
}
