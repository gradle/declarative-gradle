package org.gradle.api.experimental.kmp.testing;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.Nested;

/**
 * Represents a custom suite of tests for a KMP project.
 */
public interface TestSuite {
    DirectoryProperty getSourceRoot();

    @Nested
    TestingDependencies getDependencies();
}
