package io.github.iromul.media

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import io.github.iromul.media.scripts.AssignArtworkToMediaFilesScript
import io.github.iromul.media.scripts.BuildArtworkCacheScript
import io.github.iromul.media.scripts.OrganizeAndCopyFilesScript
import io.github.iromul.media.scripts.config.Config

class MediaOrganizerCommand : NoOpCliktCommand(
    name = "media-organizer",
    printHelpOnEmptyArgs = true
)

class CopyMediaCommand : CliktCommand(
    name = "copy",
    help = "Organize and copy media files to DEST",
    printHelpOnEmptyArgs = true
) {

    private val mediaRoot by option("-m", "--media-root", help = "Media directory root")
        .file(mustExist = true, canBeFile = false, mustBeReadable = true)
        .required()

    private val dryRun by option("--dry-run", "-D", help = "Do not perform any file operations")
        .flag()

    private val output by argument(name = "DEST")
        .file(mustExist = true, canBeFile = false, mustBeWritable = true)

    override fun run() {
        Config.mediaRoot = mediaRoot

        val organizer = OrganizeAndCopyFilesScript(mediaRoot, output, dryRun)

        organizer.perform()
    }
}

class BuildArtworkCacheCommand : CliktCommand(
    name = "artwork-cache",
    help = "Building artwork cache using public Apple Music API",
    printHelpOnEmptyArgs = true
) {

    private val mediaRoot by option("-m", "--media-root", help = "Media directory root")
        .file(mustExist = true, canBeFile = false, mustBeReadable = true)
        .required()

    override fun run() {
        Config.mediaRoot = mediaRoot

        BuildArtworkCacheScript(mediaRoot).perform()
    }
}

class AssignCoversToMediaFilesCommand : CliktCommand(
    name = "artwork-assign",
    help = "Assign artwork to media files using artwork cache",
    printHelpOnEmptyArgs = true
) {

    private val mediaRoot by option("-m", "--media-root", help = "Media directory root")
        .file(mustExist = true, canBeFile = false, mustBeReadable = true)
        .required()

    private val dryRun by option("--dry-run", "-D", help = "Do not perform any file operations")
        .flag()

    override fun run() {
        Config.mediaRoot = mediaRoot

        AssignArtworkToMediaFilesScript(mediaRoot, dryRun).perform()
    }
}

fun main(args: Array<String>) =
    MediaOrganizerCommand()
        .subcommands(CopyMediaCommand())
        .subcommands(BuildArtworkCacheCommand())
        .subcommands(AssignCoversToMediaFilesCommand())
        .main(args)
