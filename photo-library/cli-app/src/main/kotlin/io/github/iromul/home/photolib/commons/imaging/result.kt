package io.github.iromul.home.photolib.commons.imaging

import org.apache.commons.imaging.common.ImageMetadata

sealed interface ImageMetadataResult

data class MetadataOk(
    val imageMetadata: ImageMetadata,
) : ImageMetadataResult

data object UnsupportedFormat : ImageMetadataResult

data object MetadataEmpty : ImageMetadataResult

data class MetadataReadError(
    val e: Throwable? = null
) : ImageMetadataResult
