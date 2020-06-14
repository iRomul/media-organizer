package io.github.iromul.media.artwork.file

import java.io.File

class FileHandledImageFile(
    override val size: Int,
    override val mime: String,
    private val file: File
) : ImageFile() {

    override fun bytes() = file.readBytes()
}
