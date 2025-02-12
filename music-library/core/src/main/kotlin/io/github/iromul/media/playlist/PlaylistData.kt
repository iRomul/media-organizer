package io.github.iromul.media.playlist

class PlaylistData : Iterable<Entry> {

    private val entries = mutableListOf<Entry>()

    override fun iterator(): Iterator<Entry> {
        return entries.iterator()
    }

    fun addEntry(entry: Entry) {
        entries.add(entry)
    }
}
