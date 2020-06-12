package io.github.iromul.media

import java.util.logging.LogManager

object Config {

    fun logging() {
        LogManager.getLogManager().reset()
    }

    object AssignCoversToMediaFilesScript {

        var artworkSize = 500
    }

    object ArtworkCache {

        var downloadSize = 600
        var resizeSizes = listOf(500)
    }
}

fun Int.sizeToFileName(): String {
    return "${this}x${this}"
}
