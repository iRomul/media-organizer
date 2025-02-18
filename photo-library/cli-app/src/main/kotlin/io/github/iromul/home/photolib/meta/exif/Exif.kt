package io.github.iromul.home.photolib.meta.exif

import io.github.iromul.home.photolib.commons.imaging.ImagingExt
import io.github.iromul.home.photolib.commons.imaging.MetadataOk
import io.github.iromul.home.photolib.meta.Origin
import io.github.iromul.home.photolib.meta.exif.googlephoto.MetadataModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField.*
import java.time.temporal.TemporalAccessor

private val logger = KotlinLogging.logger {}

private val exifDate = DateTimeFormatterBuilder()
    .appendValue(YEAR, 4)
    .appendLiteral(':')
    .appendValue(MONTH_OF_YEAR, 2)
    .appendLiteral(':')
    .appendValue(DAY_OF_MONTH, 2)
    .appendLiteral(' ')
    .append(DateTimeFormatter.ISO_LOCAL_TIME)
    .toFormatter()

private val exifDateAlt = DateTimeFormatterBuilder()
    .appendValue(YEAR, 4)
    .appendLiteral('-')
    .appendValue(MONTH_OF_YEAR, 2)
    .appendLiteral('-')
    .appendValue(DAY_OF_MONTH, 2)
    .appendLiteral(' ')
    .append(DateTimeFormatter.ISO_LOCAL_TIME)
    .toFormatter()

sealed interface MetadataReader {

    fun canRead(): Boolean

    fun readMetadata(): MinimalMetadata
}

fun DateTimeFormatter.tryParse(text: CharSequence): TemporalAccessor? {
    return try {
        parse(text)
    } catch (ex: DateTimeParseException) {
        null
    }
}

class ExifMetadataReader(
    private val file: File
) : MetadataReader {

    private val result by lazy { ImagingExt.getMetadata(file) }

    override fun canRead(): Boolean {
        return result is MetadataOk
    }

    override fun readMetadata(): MinimalMetadata {
        val result = result

        if (result is MetadataOk) {
            val metadata = result.imageMetadata

            if (metadata is JpegImageMetadata) {
                val mm =
                    metadata.findExifValueWithExactMatch(TiffTagConstants.TIFF_TAG_DATE_TIME)?.value

                val shootDate = mm?.toString()
                    ?.let {
                        exifDate.tryParse(it) ?: exifDateAlt.tryParse(it)
                    }
                    ?.let(LocalDateTime::from)

                return MinimalMetadata(
                    shootDate,
                    null
                )
            } else {
                println("Not a jpeg exif: ${metadata.javaClass.simpleName}")
            }
        }

        return MinimalMetadata(
            null,
            null
        )
    }
}

class JsonMetadataReader(
    private val file: File
) : MetadataReader {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    override fun canRead(): Boolean {
        return file.jsonMetadataFile.exists()
    }

    override fun readMetadata(): MinimalMetadata {
        val model = json.decodeFromString<MetadataModel>(file.jsonMetadataFile.readText())

        val date = Instant.ofEpochSecond(model.photoTakenTime.timestamp)

        val o = model.googlePhotosOrigin

        val origin = when {
            o == null -> null
            o.contains("fromPartnerSharing") -> Origin.FROM_PARTNER
            o.contains("mobileUpload") -> Origin.PERSONAL
            else -> Origin.MISC
        }

        return MinimalMetadata(
            date.atOffset(ZoneOffset.UTC).toLocalDateTime(),
            origin
        )
    }

    private val File.jsonMetadataFile: File
        get() = File(parentFile, "${name}.json")
}

data class MinimalMetadata(
    val shootDate: LocalDateTime? = null,
    val origin: Origin? = null
)

fun readExif(file: File): MinimalMetadata {
    logger.info { "Reading exif at $file" }

    val readers: List<() -> MetadataReader> = listOf(
        { JsonMetadataReader(file) },
        { ExifMetadataReader(file) }
    )

    val metadataR = readers.asSequence()
        .map { it() }
        .filter { it.canRead() }
        .map { it.readMetadata() }
        .toList()

    val metadata = MinimalMetadata(
        shootDate = metadataR.firstNotNullOfOrNull { it.shootDate },
        origin = metadataR.firstNotNullOfOrNull { it.origin }
    )

    logger.info { "File ${file.name}: Metadata found ($metadata)" }

    return metadata
}
