package io.github.iromul.home.photolib.meta.exif.googlephoto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class MetadataModel(
    val creationTime: CreationTimeModel,
    val photoTakenTime: PhotoTakenTimeModel,
    val googlePhotosOrigin: JsonObject? = null,
)

@Serializable
data class CreationTimeModel(
    val timestamp: Long
)

@Serializable
data class PhotoTakenTimeModel(
    val timestamp: Long
)
