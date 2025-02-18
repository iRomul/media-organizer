package io.github.iromul.home.photolib.scripts

import io.github.iromul.home.photolib.commons.checksum.ChecksumAlg
import io.github.iromul.home.photolib.meta.exif.readExif
import io.github.iromul.home.photolib.model.FileDescriptor
import io.github.iromul.home.photolib.scripts.context.Config
import io.github.iromul.home.photolib.storage.Storage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.time.Duration
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

class BuildDbApp(
    private val storage: Storage,
    private val checksumAlg: ChecksumAlg<*>
) {

    fun run() {
        val millis = measureTimeMillis {
            runInternal()
        }

        val duration = Duration.ofMillis(millis)

        logger.info { "App running in ${duration.toSeconds()}s" }
    }

    private fun runInternal() {
        storage.clearStorage()

        val result = Result()

        runBlocking {
            Config.mediaRoot.walkTopDown()
                .filter(result.registeringIgnored { it.isFile && it.canRead() && !Files.isSymbolicLink(it.toPath()) })
                .filter(result.registeringIgnored { it.length() < Config.LENGTH_LIMIT })
                .filter(result.registeringIgnored { it.extension !in Config.excludedExtensions })
                .onEachIndexed { index, file ->
                    logger.info { "[${index.toString().padStart(5)}] Reading ${file.absolutePath}" }
                }
                .map {
                    FileDescriptor(file = it)
                }
                .asFlow()
                .map { fileDescriptor ->
                    fileDescriptor.copy(
                        checksum = checksumAlg.calc(fileDescriptor.file).toString()
                    )
                }
                .flowOn(Dispatchers.IO)
                .map { fileDescriptor ->
                    fileDescriptor.copy(
                        metadata = readExif(fileDescriptor.file)
                    )
                }
                .flowOn(Dispatchers.Default)
                .toList()
        }

//        filesToScan.forEach { fileDescriptor ->
//            fileDescriptor.checksum = checksumAlg.calc(fileDescriptor.file).toString()
//            fileDescriptor.metadata = readExif(fileDescriptor.file)
//        }

//        val hashedFiles = filesToScan.values.asSequence()
//            .filter(result.registeringUniqueAll { it.size >= 2 })
//            .flatten()
//            .onEachIndexed { index, file ->
//                logger.info { "[${index.toString().padStart(5)}] Hashing ${file.absolutePath}" }
//            }
//            .optTake(Config.DEBUG_MODE, Config.DEBUG_LIMIT)
//            .groupBy(checksumAlg::calc)
//
//        val (duplicatesEntries, uniquesEntries) = hashedFiles.entries
//            .partition { it.value.size > 1 }
//
//        val uniques = uniquesEntries.map { (_, uniques) -> uniques }.flatten()
//
//        result.duplicateGroups.putAll(duplicatesEntries.associate { (k, v) -> k to v })
//        result.uniqueFiles.addAll(uniques)
//
//        result.duplicateFiles.forEach {
//            readExif(it)
//        }
//
//        result.uniqueFiles.forEach {
//            readExif(it)
//        }
//
//        storage.storeCache(result)
    }
}
