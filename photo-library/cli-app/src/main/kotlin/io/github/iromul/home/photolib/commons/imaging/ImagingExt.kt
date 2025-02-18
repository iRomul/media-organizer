package io.github.iromul.home.photolib.commons.imaging

import io.github.iromul.home.photolib.commons.imaging.formats.jpeg.JpegImageParserExt
import org.apache.commons.imaging.AbstractImageParser
import org.apache.commons.imaging.ImagingException
import org.apache.commons.imaging.bytesource.ByteSource
import org.apache.commons.imaging.common.ImageMetadata
import org.apache.commons.imaging.formats.jpeg.JpegImageParser
import org.apache.commons.imaging.internal.ImageParserFactory
import java.io.File

object ImagingExt {

    private const val APP13_ERROR_MESSAGE = "JPEG contains more than one Photoshop App13 segment."

    fun getMetadata(file: File) =
        getMetadata(ByteSource.file(file))

    fun getMetadata(byteSource: ByteSource): ImageMetadataResult {
        return wrapResult {
            getMetadataI(byteSource)
        }
    }

    fun getMetadataI(byteSource: ByteSource): ImageMetadata? {
        val imageParser: AbstractImageParser<*> = ImageParserFactory.getImageParser(byteSource)

        return imageParser.getMetadataFix(byteSource)
    }

    private fun AbstractImageParser<*>.getMetadataFix(byteSource: ByteSource): ImageMetadata? {
        return try {
            getMetadata(byteSource, null)
        } catch (e: ImagingException) {
            if (this is JpegImageParser && e.message == APP13_ERROR_MESSAGE) {
                JpegImageParserExt().getMetadata(byteSource, null)
            } else {
                throw e
            }
        }
    }

    private fun wrapResult(body: () -> ImageMetadata?): ImageMetadataResult {
        return try {
            when (val metadata = body()) {
                null -> MetadataEmpty
                else -> MetadataOk(metadata)
            }
        } catch (e: Exception) {
            when (e) {
                is IllegalArgumentException -> UnsupportedFormat
                else -> MetadataReadError(e)
            }
        }
    }
}
