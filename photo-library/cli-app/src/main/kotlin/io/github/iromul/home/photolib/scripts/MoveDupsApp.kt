package io.github.iromul.home.photolib.scripts

import io.github.iromul.home.photolib.storage.Storage
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.Duration
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

class MoveDupsApp(
    private val storage: Storage
) {

    fun run() {
        val millis = measureTimeMillis {
            runInternal()
        }

        val duration = Duration.ofMillis(millis)

        logger.info { "App running in ${duration.toSeconds()}s" }
    }

    private fun runInternal() {
        val result = storage.retrieveCache()

        logger.info { "Stat: unique ${result.uniqueFiles.size}, dups ${result.duplicateGroups.values.flatten().size}" }

//        Config.outputs.forEach {
//            it().use { os ->
//                result.writeReport(os)
//            }
//        }
    }
}
