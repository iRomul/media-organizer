package io.github.iromul.home.photolib.scripts

import io.github.iromul.home.photolib.scripts.context.Config
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.io.OutputStream
import java.nio.file.Files

private val logger = KotlinLogging.logger {}

// Photos from 2013
data class Result(
    val ignoredFiles: MutableList<File> = mutableListOf(),
    val uniqueFiles: MutableList<File> = mutableListOf(),
    val duplicateGroups: MutableMap<Any, List<File>> = mutableMapOf(),
) {

    val duplicateFiles: List<File>
        get() = duplicateGroups.values.flatten()

    companion object {

        fun split(files: Collection<File>): Pair<File, Collection<File>> {
            return files.first() to files.drop(1)
        }
    }
}

fun Result.registeringUniqueAll(
    predicate: (Collection<File>) -> Boolean
) = { files: Collection<File> ->
    predicate(files).also {
        if (!it) {
            uniqueFiles.addAll(files)
        }
    }
}

fun Result.registeringIgnored(
    predicate: (File) -> Boolean
) = { file: File ->
    predicate(file).also {
        if (!it) {
            ignoredFiles.add(file)
        }
    }
}

fun Result.writeReport(os: OutputStream) {
    val outDir = Config.outDir

    logger.info { "Target dir: $outDir" }

    outDir.mkdirs()

    os.bufferedWriter().use { writer ->
        writer.appendLine("Duplicates")

        duplicateGroups.values.forEachIndexed { index, group ->
            val (first, rest) = Result.split(group)

            val firstPath = first.toPath()

            Files.copy(firstPath, outDir.toPath().resolve(formatFilename(first, index)))

            writer.appendLine("\t${first.absolutePath}")

            rest.forEach { file ->
                writer.appendLine("\t\t${file.absolutePath}")
            }
        }

        writer.appendLine("Uniques")

        uniqueFiles.forEachIndexed { index, file ->
            Files.copy(file.toPath(), outDir.toPath().resolve(formatFilename(file, index)))

            writer.appendLine("\t${file.absolutePath}")
        }
    }
}

fun formatFilename(file: File, index: Int) =
    "${file.nameWithoutExtension}-${index.toString().padStart(9, '0')}.${file.extension}"
