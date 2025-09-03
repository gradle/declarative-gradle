package org.gradle.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.InvalidPathException;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Static util class containing methods for extracting resource directories from the classpath.
 */
public final class ResourceLoader {
    private final ClassLoader classLoader;

    public ResourceLoader() {
        this.classLoader = ResourceLoader.class.getClassLoader();
    }

    @VisibleForTesting
    ResourceLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Recursively copies the contents of a directory from the classpath (or a jar file on the classpath)
     * to a specified directory.
     *
     * @param relativePath relative path to the source directory (or relative path within a jar file to the directory)
     * @param destDir target directory to extract the contents to
     * @throws IOException if an I/O error occurs
     */
    public void extractDirectoryFromResources(String relativePath, File destDir) throws IOException {
        URL url = classLoader.getResource(relativePath);
        if (url == null) {
            throw new IllegalArgumentException("Directory: '" + relativePath + "' not found (on the classpath loaded by: '" + classLoader + "')!");
        }

        URLConnection connection = url.openConnection();
        if (connection instanceof JarURLConnection) {
            copyDirectoryFromJar(relativePath, destDir, (JarURLConnection) connection, classLoader);
        } else {
            copyDirectory(relativePath, destDir, connection, classLoader);
        }
    }

    private static void copyDirectory(String relativePath, File destDir, URLConnection connection, ClassLoader classLoader) throws IOException {
        try {
            File file = new File(connection.getURL().toURI());
            FileUtils.copyDirectory(file, destDir);
        } catch (Exception e) {
            throw new IOException("Error extracting: '" + relativePath + "' (from the root of the classpath loaded by: '" + classLoader + "')!", e);
        }
    }

    private static void copyDirectoryFromJar(String relativePath, File destDir, JarURLConnection connection, ClassLoader classLoader) throws IOException {
        JarFile jarFile = connection.getJarFile();

        Iterator<JarEntry> iterator = jarFile.entries().asIterator();
        while (iterator.hasNext()) {
            JarEntry entry = iterator.next();
            String entryName = entry.getName();

            if (entryName.startsWith(relativePath + "/") || entryName.startsWith(relativePath + "\\")) {
                String entrySuffix = entryName.substring(relativePath.length());
                File destFile = new File(destDir, entrySuffix);
                if (!destFile.toPath().normalize().startsWith(destDir.toPath().normalize())) {
                    throw new InvalidPathException(entrySuffix, "Entry resolves to a path outside of the target directory");
                }

                if (entry.isDirectory()) {
                    FileUtils.forceMkdir(destFile);
                } else {
                    try (InputStream is = jarFile.getInputStream(entry);
                         FileOutputStream fos = new FileOutputStream(destFile)) {
                        IOUtils.copy(is, fos);
                    } catch (Exception e) {
                        throw new IOException("Error extracting: '" + entryName + "' from: '" + connection.getURL() + "' (from the root of the classpath loaded by: '" + classLoader + "')!", e);
                    }
                }
            }
        }
    }
}
