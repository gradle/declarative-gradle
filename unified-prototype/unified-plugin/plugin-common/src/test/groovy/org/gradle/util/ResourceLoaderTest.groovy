package org.gradle.util

import org.apache.commons.io.FileUtils

import spock.lang.Specification

class ResourceLoaderTest extends Specification {
    def "can extract resource directory"() {
        given:
        File output = new File("output").tap { mkdirs() }
        ResourceLoader resourceLoader = new ResourceLoader()

        when:
        resourceLoader.extractDirectoryFromResources("templates/java-library", output)

        then:
        FileUtils.listFiles(output, null, true)*.path == ['output/build.gradle.dcl', 'output/src/main/java/com/example/lib/Library.java']
    }
}
