package io.github.iromul.home

import java.io.File
import java.io.OutputStream

object Config {

    const val LENGTH_LIMIT = 1 * 1024 * 1024 * 1024 // 1Gb

    val excludedExtensions = setOf("mp4", "json", "htm", "html")

    lateinit var rootDir: File

    val outputs: List<() -> OutputStream> = listOf(
//        { System.out },
        { File(rootDir, "out.txt").outputStream().buffered() }
    )

    val dbFile: File by lazy { File(rootDir, "db.txt") }

    val outDir by lazy { File(rootDir, "out") }
}