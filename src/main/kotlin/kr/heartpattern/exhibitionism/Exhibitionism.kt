package kr.heartpattern.exhibitionism

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.ByteArrayInputStream
import java.io.FileOutputStream
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

fun transform(option: ExhibitionismOptions) {
    val logger = option.logger

    if (option.destination.exists()) {
        logger.warning("Destination file exists. Delete and process.")
        option.destination.delete()
    }
    val input = ZipFile(option.source)
    val output = ZipOutputStream(FileOutputStream(option.destination))

    val resultQueue = LinkedBlockingQueue<Pair<String, ByteArray>>()
    val transformExecutor = ThreadPoolExecutor(
        option.parallel,
        option.parallel,
        60,
        TimeUnit.SECONDS,
        LinkedBlockingQueue()
    )
    var totalCount = 0

    logger.info("Start transformation")

    for (entry in input.entries()) {
        if (entry.name.endsWith(".class") && option.path.any { entry.name.startsWith(it) }) {
            logger.fine("Transform class: ${entry.name}")
            val bytes = input.getInputStream(entry)
            transformExecutor.execute {
                val reader = ClassReader(bytes)
                val writer = ClassWriter(0)
                val transformer = ExhibitionismTransformer(writer, option)
                reader.accept(transformer, ClassReader.EXPAND_FRAMES)
                resultQueue.put(
                    entry.name to writer.toByteArray()
                )
            }
            totalCount++
        } else {
            logger.fine("Copy resource: ${entry.name}")
            output.putNextEntry(ZipEntry(entry.name))
            ByteArrayInputStream(input.getInputStream(entry).readBytes()).use {
                it.copyTo(output)
            }
            output.closeEntry()
        }
    }
    input.close()

    var completeCount = 0
    while (completeCount < totalCount) {
        val (name, bytes) = resultQueue.take()
        output.putNextEntry(ZipEntry(name))
        output.write(bytes)
        output.closeEntry()
        completeCount++
        if (completeCount % 1000 == 0) {
            logger.info("Processing...  (${completeCount}/${totalCount})")
        }
    }

    output.close()
    logger.info("Complete transformation")
}