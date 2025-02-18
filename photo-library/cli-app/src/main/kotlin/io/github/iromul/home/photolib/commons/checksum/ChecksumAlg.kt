package io.github.iromul.home.photolib.commons.checksum

import io.github.iromul.home.photolib.scripts.context.Config
import java.io.File
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.zip.Adler32

sealed interface ChecksumAlg<R : Any> {

    fun calc(file: File): R
}

data object DigestChecksumAlg : ChecksumAlg<String> {

    private val digest = MessageDigest.getInstance("SHA-256")

    override fun calc(file: File): String {
        digest.reset()

        file.inputStream().use { fis ->
            DigestInputStream(fis, digest).use {
                val buffer = ByteArray(Config.INPUT_STREAM_BUFFER_SIZE) // 2MB buffer
                while (it.read(buffer) != -1) {
                    // Reading file data via DigestInputStream
                    // Checksum is being updated automatically
                }
            }
        }

        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}

data object AdlerChecksumAlg : ChecksumAlg<Long> {

    private val adler32 = Adler32()

    override fun calc(file: File): Long {
        adler32.reset()

        TODO()

        return adler32.value
    }
}
