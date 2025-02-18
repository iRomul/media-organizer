package io.github.iromul.home.photolib.draft

import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants
import java.io.File
import java.io.FileFilter
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField.*

fun File.children(): List<File> {
    return listFiles()?.toList() ?: emptyList()
}

fun File.children(filter: (File) -> Boolean): List<File> {
    return listFiles(FileFilter { filter(it) })?.toList() ?: emptyList()
}

val isFileFilter: (File) -> Boolean = { it.isFile }

private val photoFileNameTimestamp = DateTimeFormatter.BASIC_ISO_DATE
private val exifDate = DateTimeFormatterBuilder()
    .appendValue(YEAR, 4)
    .appendLiteral(':')
    .appendValue(MONTH_OF_YEAR, 2)
    .appendLiteral(':')
    .appendValue(DAY_OF_MONTH, 2)
    .appendLiteral(' ')
    .append(DateTimeFormatter.ISO_LOCAL_TIME)
    .toFormatter()

private const val BASE_ID = 10000

fun main() {
//    val inputPathStr = "D:\\Utility\\Backups\\Photos\\Wedding\\2023-08-19-001-wedding-reg\\Normal"
//    val inputPathStr = "D:\\Utility\\Backups\\Photos\\Wedding\\2023-08-19-001-wedding-reg\\BW"
    val inputPathStr = "D:\\Utility\\Backups\\Photos\\Wedding\\2023-08-19-002-wedding-banquet"
    val inputPath = Path.of(inputPathStr)

    val outputPath = inputPath.resolve("out")
    val outputDir = outputPath.toFile().also {
        it.mkdirs()
    }

    val photoDir = File(inputPathStr).also {
        require(it.exists()) { "Folder should exist" }
        require(it.isDirectory) { "Should be a directory" }
        require(it.canWrite() && it.canRead()) { "Should be writeable/readable" }
        require(it.children(isFileFilter).isNotEmpty()) { "Should contain at least 1 file" }
    }

    photoDir
        .children(isFileFilter)
        .forEachIndexed { _, file ->
//            println(file.name)
            val match = """1-(\d+)(.*)\.jpg""".toRegex(RegexOption.IGNORE_CASE).matchEntire(file.name)!!

            val photoId = (match.groups[1]!!.value.toLong() + BASE_ID)

            val baseDate = LocalDate.of(2023, Month.AUGUST, 19)
            val registrationTime = LocalDateTime.of(baseDate, LocalTime.of(16, 0, 0))

            val dateStamp = baseDate.format(photoFileNameTimestamp)

            val eventTime = registrationTime.plusSeconds((photoId - BASE_ID) * 10)

            val metadata = Imaging.getMetadata(file) as JpegImageMetadata

            val outputSet = metadata.exif.outputSet
            val exifDirectory = outputSet.orCreateExifDirectory

            // YYYY:MM:DD HH:MM:SS
            val exifDateAscii = eventTime.format(exifDate)

            exifDirectory.removeField(TiffTagConstants.TIFF_TAG_DATE_TIME)
            exifDirectory.add(TiffTagConstants.TIFF_TAG_DATE_TIME, exifDateAscii)

            exifDirectory.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL)
            exifDirectory.add(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, exifDateAscii)

//            outputSet.setGPSInDegrees(30.373405, 59.928361)
//            outputSet.setGPSInDegrees(30.265221, 59.885900)

            val targetFilename = "$dateStamp-$photoId-wedding-banquet.jpg"
            val outFile = File(outputDir, targetFilename)

            outFile.outputStream().buffered().use {
                ExifRewriter().updateExifMetadataLossless(file, it, outputSet)
            }

            println("$targetFilename @ $eventTime @ $exifDateAscii")
        }
}
