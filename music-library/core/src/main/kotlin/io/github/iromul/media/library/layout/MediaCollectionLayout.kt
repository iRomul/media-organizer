package io.github.iromul.media.library.layout

import io.github.iromul.media.excludeRoot
import java.io.File
import java.nio.file.Path

class MediaCollectionLayout(
    private val mediaRoot: File,
    private val typeHandlers: List<MediaCollectionTypeHandler>
) {

    fun isMediaCollectionDirectory(file: File): Boolean {
        return findMediaCollectionTypeHandler(file) != null
    }

    fun findMediaCollectionTypeHandler(file: File): MediaCollectionTypeHandler? {
        return typeHandlers.firstOrNull { it.matches(file, file.mediaRootRelativePath) }
    }

    private val File.mediaRootRelativePath: Path
        get() = toPath().excludeRoot(mediaRoot.toPath())
}
