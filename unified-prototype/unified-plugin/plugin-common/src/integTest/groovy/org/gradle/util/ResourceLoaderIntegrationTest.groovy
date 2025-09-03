package org.gradle.util

import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.InvalidPathException
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

import static org.gradle.test.util.TestAssertions.assertDirContainsExactly

class ResourceLoaderIntegrationTest extends Specification {
    @TempDir
    File tempDir

    private File outputDir = new File("build/tmp/integTest/output").tap {
        deleteDir()
        mkdirs()
    }

    def "can load resource from jar file"() {
        given:
        ResourceLoader resourceLoader = new ResourceLoader()

        when:
        resourceLoader.extractDirectoryFromResources("templates/java-library", outputDir)

        then:
        assertDirContainsExactly(outputDir, ['build.gradle.dcl', 'src/main/java/com/example/lib/Library.java'])
    }

    def "throws exception when resource resolves to a file outside of the target directory"() {
        given:
        File poisonJar = createPoisonJarFile()
        URLClassLoader poisonClassLoader = new URLClassLoader([poisonJar.toURI().toURL()] as URL[])
        ResourceLoader resourceLoader = new ResourceLoader(poisonClassLoader)

        when:
        resourceLoader.extractDirectoryFromResources("foo", outputDir)

        then:
        def e = thrown(InvalidPathException)
        e.message == "Entry resolves to a path outside of the target directory: /../poison.txt"

        and:
        !new File(outputDir.parentFile, "poison.txt").exists()
    }

    File createPoisonJarFile() {
        File poisonJar = new File(tempDir, "poison.jar")
        new JarOutputStream(new FileOutputStream(poisonJar)).withCloseable { jarOut ->
            def foo = new JarEntry("foo/")
            jarOut.putNextEntry(foo)
            jarOut.closeEntry()
            def poison = new JarEntry("foo/../poison.txt")
            jarOut.putNextEntry(poison)
            jarOut.write("This is poison".bytes)
            jarOut.closeEntry()
        }
        return poisonJar
    }
}
