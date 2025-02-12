package io.github.iromul.home

import java.io.File
import java.nio.file.Files

fun main(args: Array<String>) {
    require(args.size == 1) { "Expected exactly one argument" }

    val checksumAlg: ChecksumAlg<*> = DigestChecksumAlg

    val rootDir = File(args[0])

    if (!rootDir.isDirectory) {
        error("Expected directory ${rootDir.absolutePath}")
    }

    Config.rootDir = rootDir

    val result = Result()

    val filesBySize = rootDir.walkTopDown()
        .filter(result.registeringIgnored { it.isFile && it.canRead() })
        .filter(result.registeringIgnored { it.length() < Config.LENGTH_LIMIT })
        .filter(result.registeringIgnored { it.extension !in Config.excludedExtensions })
        .onEachIndexed { index, file ->
            println("[${index.toString().padStart(5)}] Reading ${file.absolutePath}")
        }
        .groupBy { Files.size(it.toPath()) }

    val duplicates = filesBySize.asSequence()
        .map { it.value }
        .filter { it.size >= 2 }
        .flatten()
        .onEachIndexed { index, file ->
            println("[${index.toString().padStart(5)}] Hashing ${file.absolutePath}")
        }
//        .take(500) // TODO: remove
        .groupBy(checksumAlg::calc)
        .filterValues { it.size > 1 }

    result.duplicateGroups.addAll(duplicates.values)

    writeDb(Config.dbFile.outputStream(), duplicates)
}