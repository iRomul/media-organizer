package io.github.iromul.media.commons.itunes.model

data class Entries(
    var resultCount: Int? = null,
    var results: List<Entry> = emptyList()
)
