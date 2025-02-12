package io.github.iromul.home

import java.io.File
import java.io.OutputStream
import java.nio.file.Files
import java.security.DigestInputStream
import java.security.MessageDigest
import kotlin.io.path.div

sealed interface ChecksumAlg<R : Any> {

    fun calc(file: File): R
}

data object DigestChecksumAlg : ChecksumAlg<String> {

    private val digest = MessageDigest.getInstance("SHA-256")

    override fun calc(file: File): String {
        digest.reset()

        file.inputStream().use { fis ->
            DigestInputStream(fis, digest).use {
                val buffer = ByteArray(2 * 1024 * 1024) // 2MB buffer
                while (it.read(buffer) != -1) {
                    // Reading file data via DigestInputStream
                    // Checksum is being updated automatically
                }
            }
        }

        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}

/**
 * A comparator that prioritizes a specific value, ensuring it appears first in the sorted list.
 *
 * @param T The type of elements compared.
 * @property specialValue The value to prioritize at the top.
 * @property fallbackComparator The comparator to use for other elements. If null, natural ordering is used.
 * @throws IllegalArgumentException If fallbackComparator is null and T does not implement Comparable.
 */
//class SpecialValueComparator<T>(
//    private val specialValue: T,
//    private val fallbackComparator: Comparator<T>
//) : Comparator<T> {
//
//    private val actualFallbackComparator: Comparator<T> =
//        fallbackComparator
//            ?: run {
//                // Use natural ordering if no fallback comparator is provided
//                when {
//                    Comparable::class.java.isAssignableFrom(specialValue::class.java) -> Comparator { a, b ->
//                        @Suppress("UNCHECKED_CAST")
//                        (a as Comparable<T>).compareTo(b)
//                    }
//
//                    else -> throw IllegalArgumentException(
//                        "fallbackComparator is null and T does not implement Comparable."
//                    )
//                }
//            }
//
//    override fun compare(o1: T, o2: T): Int {
//        val isO1Special = specialValue == o1
//        val isO2Special = specialValue == o2
//
//        return when {
//            isO1Special && isO2Special -> 0 // Both are special
//            isO1Special -> -1 // Only o1 is special
//            isO2Special -> 1  // Only o2 is special
//            else -> actualFallbackComparator.compare(o1, o2) // Neither is special
//        }
//    }
//}

// Photos from 2013
data class Result(
    val ignoredFiles: MutableList<File> = mutableListOf(),
    val duplicateGroups: MutableList<List<File>> = mutableListOf(),
) {

    fun registeringIgnored(predicate: (File) -> Boolean): (File) -> Boolean {
        return { file ->
            val check = predicate(file)

            if (!check) {
                ignoredFiles.add(file)
            }

            check
        }
    }

    companion object {

        fun split(files: Collection<File>): Pair<File, Collection<File>> {
            return files.first() to files.drop(1)
        }
    }
}

fun Result.writeReport(os: OutputStream) {
    val outDir = Config.outDir

    outDir.mkdirs()

    os.bufferedWriter().use { writer ->
        writer.appendLine("Duplicates")

        duplicateGroups.forEachIndexed { index, group ->
            val (first, rest) = Result.split(group)

            val firstPath = first.toPath()

            Files.copy(firstPath, outDir.toPath() / formatFilename(first, index))

            writer.appendLine("\t${first.absolutePath}")

            rest.forEach { file ->
                writer.appendLine("\t\t${file.absolutePath}")
            }
        }

//        writer.appendLine("Ignored files")
//
//        result.ignoredFiles.forEach { file ->
//            writer.appendLine("\t${file.name}")
//        }
    }
}

fun formatFilename(file: File, index: Int) =
    "${file.nameWithoutExtension}-${index.toString().padStart(9, '0')}.${file.extension}"

fun main(args: Array<String>) {
    require(args.size == 1) { "Expected exactly one argument" }

    val rootDir = File(args[0])

    if (!rootDir.isDirectory) {
        error("Expected directory ${rootDir.absolutePath}")
    }

    Config.rootDir = rootDir

    val duplicates = readDb(Config.dbFile.inputStream())

    val result = Result(mutableListOf(), duplicates.values.toMutableList())

    Config.outputs.forEach {
        it().use { os ->
            result.writeReport(os)
        }
    }
}