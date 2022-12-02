package io.github.iromul.media

import java.io.File
import java.nio.file.Path

fun Path.excludeRoot(exclusion: Path): Path {
    val diff = this - exclusion

    return if (diff.isNotEmpty()) {
        diff.reduce { acc, path -> acc.resolve(path) }
    } else {
        this
    }
}

fun String.sanitizeWindowsFileName() = replace("[<>:\"/\\\\|?*]".toRegex(), "_").run {
    (if (endsWith(".")) this + "_" else this).trimEnd()
}

val Path.extension: String get() = fileName.toString().substringAfterLast(".")
val Path.dropExtension: String get() = fileName.toString().substringBeforeLast(".")

fun File.listFilesSafely(): List<File> = listFiles()?.toList() ?: emptyList()
