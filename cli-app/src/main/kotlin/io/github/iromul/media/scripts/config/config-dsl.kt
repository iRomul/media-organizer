package io.github.iromul.media.scripts.config

import io.github.iromul.media.library.layout.MediaCollectionTypeHandler
import io.github.iromul.media.library.layout.order.CollectionOrder
import java.io.File
import java.nio.file.Path

fun byRelativePath(matcher: Path.() -> Boolean) =
    { _: File, relativePath: Path -> relativePath.matcher() }

fun Path.nestingLevel() = nameCount - 1

fun Path.endsWith(matcher: (String) -> Boolean): Boolean {
    return matcher(last().toString())
}

fun registerMediaCollectionType(
    name: String,
    matcher: (collectionDirectory: File, relativePath: Path) -> Boolean,
    mediaFilesMatcher: (mediaFile: File) -> Boolean,
    order: CollectionOrder
) =
    MediaCollectionTypeHandler(
        name,
        matcher,
        mediaFilesMatcher,
        order
    )