package io.github.iromul.media.artwork.file

class BinaryImageFile(
    override val size: Int,
    override val mime: String,
    private val byteArray: ByteArray
) : ImageFile() {

    override fun bytes() = byteArray
}
