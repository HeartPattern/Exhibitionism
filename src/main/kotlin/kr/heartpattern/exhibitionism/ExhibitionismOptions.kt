package kr.heartpattern.exhibitionism

import java.io.File
import java.util.logging.Logger

data class ExhibitionismOptions(
    val source: File,
    val destination: File,
    val logger: Logger = Logger.getGlobal(),
    val parallel: Int = 1,
    val path: Set<String> = setOf(),
    val public: Boolean = true,
    val open: Boolean = true,
    val ignoreStaticFinal: Boolean = false,
    val fixDuplicates: Boolean = false,
    val fixInvalidAccess: Boolean = true,
)
