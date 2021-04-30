package kr.heartpattern.exhibitionism

import me.tongfei.progressbar.ProgressBar
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.ByteArrayInputStream
import java.io.FileOutputStream
import java.util.concurrent.Executors
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

    val resultThread = Executors.newSingleThreadExecutor()
    val transformThread = Executors.newFixedThreadPool(option.parallel)

    logger.fine("Start transformation")
    val progress = ProgressBar("Transform", 0)

    for (entry in input.entries()) {
        progress.maxHint(progress.max + 1)
        val bytes = input.getInputStream(entry).readBytes()

        if (entry.name.endsWith(".class")
            && (option.path.isEmpty() || option.path.any { entry.name.startsWith(it) })) {
            transformThread.execute {
                val reader = ClassReader(bytes)
                val writer = ClassWriter(0)
                val transformer = ExhibitionismTransformer(writer, option)
                reader.accept(transformer, ClassReader.EXPAND_FRAMES)
                resultThread.execute{
                    output.putNextEntry(ZipEntry(entry.name))
                    output.write(writer.toByteArray())
                    output.closeEntry()
                    progress.step()
                }
            }
        } else {
            resultThread.execute{
                output.putNextEntry(ZipEntry(entry.name))
                ByteArrayInputStream(bytes).use {
                    it.copyTo(output)
                }
                output.closeEntry()
                progress.step()
            }
        }
    }


    transformThread.shutdown()
    transformThread.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)

    resultThread.shutdown()
    resultThread.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)

    input.close()
    output.close()
    logger.fine("Complete transformation")
}
