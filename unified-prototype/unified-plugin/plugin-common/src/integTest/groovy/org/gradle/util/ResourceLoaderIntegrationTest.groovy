package org.gradle.util

import org.apache.commons.io.FileUtils
import spock.lang.Specification

class ResourceLoaderIntegrationTest extends Specification {
    File outputDir

    def setup() {
        outputDir = new File("build/tmp/integTest/output").tap { deleteDir() }
    }

    def "can load resource from jar file"() {
        given:
        ResourceLoader resourceLoader = new ResourceLoader()

        when:
        resourceLoader.extractDirectoryFromResources("templates/java-library", outputDir)

        then:
        assertOutputIs(['build.gradle.dcl', 'src/main/java/com/example/lib/Library.java'])
    }

    private void assertOutputIs(List<String> expectedRelativePaths) {
        def actualPaths = FileUtils.listFiles(outputDir, null, true)*.path
        def expectedPaths = expectedRelativePaths.collect { "${outputDir.toPath()}/$it".toString() }
        assert actualPaths == expectedPaths
    }
}
