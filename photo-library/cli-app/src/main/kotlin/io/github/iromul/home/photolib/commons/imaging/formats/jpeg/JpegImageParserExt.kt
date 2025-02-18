package io.github.iromul.home.photolib.commons.imaging.formats.jpeg

import org.apache.commons.imaging.bytesource.ByteSource
import org.apache.commons.imaging.formats.jpeg.JpegConstants
import org.apache.commons.imaging.formats.jpeg.JpegImageParser
import org.apache.commons.imaging.formats.jpeg.JpegImagingParameters
import org.apache.commons.imaging.formats.jpeg.JpegPhotoshopMetadata
import org.apache.commons.imaging.formats.jpeg.iptc.PhotoshopApp13Data
import org.apache.commons.imaging.formats.jpeg.segments.App13Segment

class JpegImageParserExt : JpegImageParser() {

    override fun getPhotoshopMetadata(byteSource: ByteSource, params: JpegImagingParameters): JpegPhotoshopMetadata? {
        val abstractSegments = readSegments(byteSource, intArrayOf(JpegConstants.JPEG_APP13_MARKER), false)

        if (abstractSegments == null || abstractSegments.isEmpty()) {
            return null
        }

        var photoshopApp13Data: PhotoshopApp13Data? = null

        for (s in abstractSegments) {
            val segment = s as App13Segment

            val data = segment.parsePhotoshopSegment(params)
            if (data != null) {
                photoshopApp13Data = data

                break
            }
        }

        if (null == photoshopApp13Data) {
            return null
        }

        return JpegPhotoshopMetadata(photoshopApp13Data)
    }
}
