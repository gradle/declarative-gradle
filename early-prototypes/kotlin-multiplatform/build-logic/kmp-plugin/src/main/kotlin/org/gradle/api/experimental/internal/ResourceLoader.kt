package org.gradle.api.experimental.internal

import com.google.common.base.Preconditions
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Utility class for loading resources from files on the classpath.
 */
object ResourceLoader {
    private const val RESOURCE_DIR_PREFIX = "resource"

    /**
     * Get an [InputStream] to the specified resource.
     *
     * Note that this method is intended to retrieve resources from within a jar file.
     * So it must *NOT* utilize [ClassLoader.getResource], which will fail to extract files from
     * inside jars on the classpath.  Instead, use [ClassLoader.getResourceAsStream].
     *
     * @param relativePath complete path from root of the classpath to a resource file, or a file within a jar on the classpath
     * @return a stream referencing the specified file
     */
    @JvmStatic
    fun getResourceAsStream(relativePath: String): InputStream {
        Preconditions.checkArgument(relativePath.isNotBlank(), "relativePath must NOT be empty!")

        val classLoader = Thread.currentThread().contextClassLoader
        val result = classLoader.getResourceAsStream(relativePath)

        if (null != result) {
            return result
        } else {
            throw FileNotFoundException("File $relativePath could not be found (from the root of the classpath used by: $classLoader)!")
        }
    }

    /**
     * Get a resource by extracting the contents of the file at the given path and rewriting them to a
     * temporary file, which can be safely accessed.
     *
     * Note that this method is intended to retrieve resources from within a jar file.
     * So it must *NOT* utilize [ClassLoader.getResource], which will fail to extract files from
     * inside jars on the classpath.
     *
     * @param relativePath complete path from root of the classpath to a resource file, or a file within a jar on the classpath
     * @return a file object containing the same contents as the specified file
     */
    @JvmStatic
    fun getResourceAsTempFile(relativePath: String): File {
        val fileName = Paths.get(relativePath).fileName.toString()
        val tempDirectory = Files.createTempDirectory(RESOURCE_DIR_PREFIX)
        val resourceFile = Files.createFile(Paths.get(tempDirectory.toString(), fileName)).toFile()

        val inputStream = getResourceAsStream(relativePath)
        val outputStream = FileOutputStream(resourceFile)
        inputStream.copyTo(outputStream)

        return resourceFile
    }

    /**
     * Get a specified file resource from the classpath.
     *
     * Note that this method can <strong>NOT</strong> be used to retrieve resources from within a jar file.
     * Use [getResourceAsStream] or [getResourceAsTempFile] for that use case
     *
     * @param relativePath complete path from root of the classpath to a resource file <strong>NOT</strong> contained in a jar
     * @return a file object referencing the file
     */
    @JvmStatic
    fun getResource(relativePath: String): File {
        Preconditions.checkArgument(relativePath.isNotBlank(), "relativePath must NOT be empty!")

        val classLoader = Thread.currentThread().contextClassLoader
        val result = classLoader.getResource(relativePath)

        if (null != result) {
            return File(result.toURI())
        } else {
            throw FileNotFoundException("File $relativePath could not be found (from the root of the classpath used by: $classLoader)!")
        }
    }
}