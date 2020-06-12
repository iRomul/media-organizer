package io.github.iromul.media.artwork

import io.github.iromul.media.sizeToFileName
import java.io.File

class Images(
    private val directory: File
) {

    operator fun get(size: Int, ext: String): Image {
        return Image(size, ext, directory)
    }
}

class Image(
    size: Int,
    ext: String,
    directory: File
) {

    val file = File(directory, "${size.sizeToFileName()}.$ext")
    private val missedFile = File(directory, "${size.sizeToFileName()}.missed")

    fun get(): File {
        return file
    }

    fun write(image: ByteArray) = file.writeBytes(image)

    fun read() = file.readBytes()

    fun markAsMissed() {
        missedFile.createNewFile()
    }

    val isExists: Boolean
        get() = file.exists()

    val isNotExists: Boolean
        get() = !isExists

    val isMissed: Boolean
        get() = missedFile.exists()
}
