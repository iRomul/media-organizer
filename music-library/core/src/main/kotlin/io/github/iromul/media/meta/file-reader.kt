package io.github.iromul.media.meta

import io.github.iromul.media.playlist.Entry
import io.github.iromul.media.playlist.PlaylistData
import java.io.File
import java.nio.charset.Charset

fun loadPlaylistData(file: File): PlaylistData {
    val data = PlaylistData()

    file.useLines(Charset.forName("UTF-16")) { lineSequence ->
        lineSequence.drop(1).forEach { line ->
            val columns = line.split("\t")

            data.addEntry(Entry(title = columns[0], artist = columns[1], composer = columns[2], album = columns[3]))
        }
    }

    return data
}
