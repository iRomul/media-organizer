package io.github.iromul.home.photolib.scripts.context

import org.h2.jdbcx.JdbcDataSource
import java.io.File
import java.io.OutputStream
import java.sql.Connection
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.sql.DataSource

object Config {

    const val DEBUG_MODE = false
    const val DEBUG_LIMIT = 2500

    const val INPUT_STREAM_BUFFER_SIZE = 1 * 1024 * 1024 // 1MB buffer

    const val LENGTH_LIMIT = 1 * 1024 * 1024 * 1024 // 1Gb

    val excludedExtensions = setOf("json", "htm", "html", "vcf")

    var isDryRun: Boolean = false

    lateinit var mediaRoot: File

    val outputs: List<() -> OutputStream> = listOf(
//        { System.out },
        { File(mediaRoot, "out.txt").outputStream().buffered() }
    )

    val outDir by lazy {
        val up = mediaRoot.toPath().parent

        File(up.toFile(), "Sorted_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))}")
    }

    private val dataSource: DataSource by lazy {
        Class.forName("org.h2.Driver")

        JdbcDataSource().apply {
            setURL("jdbc:h2:file:./test.sqlite;DB_CLOSE_DELAY=-1")
        }
    }

    val connection: Connection by lazy {
        dataSource.connection
    }
}
