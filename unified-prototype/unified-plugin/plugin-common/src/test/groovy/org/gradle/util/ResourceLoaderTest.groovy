package org.gradle.util

import org.apache.commons.io.FileUtils

import spock.lang.Specification

class ResourceLoaderTest extends Specification {
    def "can load resource from jar file"() {
        given:
        File output = new File("output").tap { mkdirs() }
        ResourceLoader resourceLoader = new ResourceLoader(this.getClass().getClassLoader())

        when:
        File templatesDir = resourceLoader.getResource("templates/java-library")
        FileUtils.copyDirectory(templatesDir, output)

        then:
        FileUtils.listFiles(output, null, true)*.path == ['output/build.gradle.dcl', 'output/src/main/java/com/example/lib/Library.java']
    }
}
