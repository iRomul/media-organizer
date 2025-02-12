package io.github.iromul.media.library

import io.github.iromul.media.library.collection.MediaCollection
import java.io.File

class MediaLibrary(
    val mediaRoot: File,
    val mediaCollections: List<MediaCollection>
)