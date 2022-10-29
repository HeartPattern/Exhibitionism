package kr.heartpattern.exhibitionism

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import java.io.File
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.system.exitProcess

class App : CliktCommand() {
    private val input: File by option().file(mustExist = true).required()
    private val output: File by option().file(mustExist = false).required()
    private val parallel: Int by option().int().default(Runtime.getRuntime().availableProcessors())
    private val nopublic: Boolean by option().flag()
    private val noopen: Boolean by option().flag()
    private val path: List<String> by option().split(",").defaultLazy { listOf() }
    private val ignoreStaticFinal: Boolean by option().flag()
    private val ignoreDuplicates: Boolean by option().flag()
    private val fixInvalidAccess: Boolean by option().flag(default = true)

    override fun run() {
        val logger = Logger.getLogger("Exhibitionism")
        val console = ConsoleHandler()
        logger.useParentHandlers = false
        logger.addHandler(console)
        logger.level = Level.ALL
        console.formatter = SimpleFormatter()
        console.level = Level.ALL
        transform(
            ExhibitionismOptions(
                source = input,
                destination = output,
                parallel = parallel,
                path = path.map { it.replace('.', '/') }.toSet(),
                logger = logger,
                public = !nopublic,
                open = !noopen,
                ignoreStaticFinal = ignoreStaticFinal,
                fixDuplicates = ignoreDuplicates,
                fixInvalidAccess = fixInvalidAccess,
            )
        )
        exitProcess(0)
    }
}

fun main(args: Array<String>) = App().main(args)
