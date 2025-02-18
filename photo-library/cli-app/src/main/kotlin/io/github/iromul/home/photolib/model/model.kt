package io.github.iromul.home.photolib.model

import io.github.iromul.home.photolib.meta.exif.MinimalMetadata
import java.io.File

data class FileDescriptor(
    var file: File,
    var checksum: String? = null,
    var metadata: MinimalMetadata? = null
)
