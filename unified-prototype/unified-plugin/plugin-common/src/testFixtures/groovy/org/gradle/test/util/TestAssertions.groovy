package org.gradle.test.util

import org.gradle.internal.impldep.org.apache.commons.io.FileUtils

/**
 * Static util class containing test assertions.
 */
class TestAssertions {
    private TestAssertions() { /* not instantiable */ }

    static void assertDirContainsExactly(File outputDir, List<String> expectedRelativePaths) {
        def actualPaths = FileUtils.listFiles(outputDir, null, true)*.path.sort()
        def expectedPaths = expectedRelativePaths.collect { new File(outputDir, it).path }.sort()
        assert actualPaths == expectedPaths
    }
}
