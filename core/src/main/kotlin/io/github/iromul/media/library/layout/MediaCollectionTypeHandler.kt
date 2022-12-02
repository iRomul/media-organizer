package io.github.iromul.media.library.layout

import io.github.iromul.media.library.layout.order.CollectionOrder
import java.io.File
import java.nio.file.Path

data class MediaCollectionTypeHandler(
    val name: String,
    val matcher: (collectionDirectory: File, relativePath: Path) -> Boolean,
    val mediaFilesMatcher: (mediaFile: File) -> Boolean,
    val order: CollectionOrder
) {

    fun matches(collectionDirectory: File, relativePath: Path) = matcher(collectionDirectory, relativePath)
}