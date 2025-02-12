package io.github.iromul.media.library.collection

fun MediaCollectionType.stringify() = name

fun MediaCollection.stringify() = "${type.stringify()} \"$name\""

fun MediaFile.stringify(includeTrack: Boolean = false, includeFile: Boolean = false): String {
    val minimal = listOf(artist, album, title).joinToString(separator = " - ") { it.notNullOrBlankOrElse("<N/A>") }

    var result = minimal

    if (includeTrack) {
        result = "$track. $result"
    }

    if (includeFile) {
        result += " (file $file)"
    }

    return result
}

private fun String?.notNullOrBlankOrElse(default: String) = if (isNullOrBlank()) default else this
