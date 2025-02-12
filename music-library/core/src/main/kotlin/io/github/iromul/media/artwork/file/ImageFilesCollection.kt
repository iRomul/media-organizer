package io.github.iromul.media.artwork.file

import java.util.*

class ImageFilesCollection : Collection<ImageFile> {

    companion object {

        val Empty = ImageFilesCollection()
    }

    constructor()

    constructor(imageFiles: Iterable<ImageFile>) {
        imageFiles.forEach { file ->
            add(file)
        }
    }

    private val images = TreeSet<ImageFile>()

    fun add(imageFile: ImageFile) {
        images.add(imageFile)
    }

    fun findImageFileByMimeAndSize(mime: String, size: Int) = images.find { it.mime == mime && it.size == size }

    override val size: Int
        get() = images.size

    override fun contains(element: ImageFile) = images.contains(element)

    override fun containsAll(elements: Collection<ImageFile>) = images.containsAll(elements)

    override fun isEmpty() = images.isEmpty()

    override fun iterator() = images.iterator()
}

