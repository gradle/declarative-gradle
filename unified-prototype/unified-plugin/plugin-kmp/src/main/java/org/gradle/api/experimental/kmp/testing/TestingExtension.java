package org.gradle.api.experimental.kmp.testing;

import org.gradle.api.tasks.Nested;

import java.util.Map;

/**
 * A DSL block that allows for configuring testing in a KMP project.
 */
public interface TestingExtension {
    @Nested
    TestingDependencies getDependencies();

    @Nested
    TestSuite getUnitTest();

    @Nested
    TestSuite getFunctionalTest();

    default Map<String, TestSuite> getTestSuites() {
        return Map.of("unitTest", getUnitTest(), "functionalTest", getFunctionalTest());
    }
}
