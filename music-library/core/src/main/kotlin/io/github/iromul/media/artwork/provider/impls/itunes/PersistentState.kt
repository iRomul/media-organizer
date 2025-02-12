package io.github.iromul.media.artwork.provider.impls.itunes

import io.github.iromul.media.sanitizeWindowsFileName
import java.io.File

class PersistentState(
    private val stateDirectory: File
) {

    fun found() {
        // no-op
    }

    fun notFound(terms: Terms) {
        stateDirectory.createMissedEntryFile(terms)
    }

    fun shouldBeSkipped(terms: Terms) = stateDirectory.missedFileExists(terms)

    private fun File.missedFileExists(terms: Terms) =
        File(this, terms.toMissedFileName()).exists()

    private fun File.createMissedEntryFile(terms: Terms) {
        mkdirs()

        val missedMarker = File(this, terms.toMissedFileName())

        missedMarker.createNewFile()
    }

    private fun Terms.toMissedFileName() =
        "${components().joinToString(" - ").sanitizeWindowsFileName()}.missed"
}
