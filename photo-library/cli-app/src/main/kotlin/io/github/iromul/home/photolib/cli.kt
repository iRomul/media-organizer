package io.github.iromul.home.photolib

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import io.github.iromul.home.photolib.commons.checksum.DigestChecksumAlg
import io.github.iromul.home.photolib.scripts.BuildDbApp
import io.github.iromul.home.photolib.scripts.MoveDupsApp
import io.github.iromul.home.photolib.scripts.context.Config
import io.github.iromul.home.photolib.storage.DbStorage

class PhotoLibraryCommand : NoOpCliktCommand(
    name = "photo-library",
    printHelpOnEmptyArgs = true
)

class BuildDbCommand : CliktCommand(
    name = "build-db",
    help = "Builds the DB at the given photos",
    printHelpOnEmptyArgs = true
) {

    private val mediaRoot by option("-m", "--media-root", help = "Media directory root")
        .file(mustExist = true, canBeFile = false, mustBeReadable = true)
        .required()

    private val dryRun by option("--dry-run", "-D", help = "Do not perform any file operations")
        .flag()

    override fun run() {
        Config.isDryRun = dryRun
        Config.mediaRoot = mediaRoot

        val app = BuildDbApp(
            DbStorage(Config.connection),
            DigestChecksumAlg
        )

        app.run()
    }
}

class MoveDuplicatesCommand : CliktCommand(
    name = "move-duplicates",
    help = "Moves the duplicate files to the given photos",
    printHelpOnEmptyArgs = true
) {

    private val mediaRoot by option("-m", "--media-root", help = "Media directory root")
        .file(mustExist = true, canBeFile = false, mustBeReadable = true)
        .required()

    private val dryRun by option("--dry-run", "-D", help = "Do not perform any file operations")
        .flag()

    override fun run() {
        Config.isDryRun = dryRun
        Config.mediaRoot = mediaRoot

        val app = MoveDupsApp(
            DbStorage(Config.connection)
        )

        app.run()
    }
}

fun main(args: Array<String>) =
    PhotoLibraryCommand()
        .subcommands(BuildDbCommand())
        .subcommands(MoveDuplicatesCommand())
        .main(args)
