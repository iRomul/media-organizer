package io.github.iromul.media.artwork.file

abstract class ImageFile : Comparable<ImageFile> {

    abstract val size: Int
    abstract val mime: String

    abstract fun bytes(): ByteArray

    override fun compareTo(other: ImageFile) = compareBy<ImageFile>({ it.mime }, { it.size }).compare(this, other)
}
