package io.github.iromul.media.scripts

import io.github.iromul.media.scripts.config.Config
import java.io.File

abstract class Script(
    protected val mediaRoot: File,
    private val dryRun: Boolean = false
) {

    init {
        Config.logging()
    }

    abstract fun perform()

    protected fun fileOperation(callback: () -> Unit) {
        if (!dryRun) {
            callback()
        }
    }
}
