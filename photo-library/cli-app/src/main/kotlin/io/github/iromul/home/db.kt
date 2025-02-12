package io.github.iromul.home

import io.github.iromul.home.DbConfig.makeLine
import io.github.iromul.home.DbConfig.parseLine
import java.io.File
import java.io.InputStream
import java.io.OutputStream

private object DbConfig {

    const val SEPARATOR = ";"

    data class DbEntry(
        val checksum: String,
        val file: File,
    )

    fun makeLine(entry: DbEntry): String {
        return "${entry.checksum}$SEPARATOR${entry.file}"
    }

    fun parseLine(line: String): DbEntry {
        val (checksum, absolutePath) = line.split(SEPARATOR)

        return DbEntry(checksum, File(absolutePath))
    }
}

fun writeDb(os: OutputStream, duplicates: Map<Any, List<File>>) {
    os.bufferedWriter().use { writer ->
        duplicates.forEach { (checksum, files) ->
            files.forEach {
                val entry = DbConfig.DbEntry(checksum.toString(), it)

                writer.appendLine(makeLine(entry))
            }
        }
    }
}

fun readDb(iss: InputStream): Map<Any, List<File>> {
    return iss.bufferedReader()
        .useLines { lines ->
            val duplicates = linkedMapOf<Any, MutableList<File>>()

            lines.forEach { line ->
                val (checksum, file) = parseLine(line)

                duplicates.putIfAbsent(checksum, mutableListOf())
                duplicates[checksum]!!.add(file)
            }

            duplicates
        }
}
