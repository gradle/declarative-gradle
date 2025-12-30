package org.gradle.api.experimental.kmp;

import org.gradle.api.Named;
import org.gradle.api.experimental.common.ApplicationDependencies;
import org.gradle.api.experimental.kmp.testing.TestingExtension;
import org.gradle.api.tasks.Nested;

/**
 * Represents a target platform in a KMP application.
 */
public interface KmpApplicationTarget extends Named {
    @Nested
    ApplicationDependencies getDependencies();

    @Nested
    TestingExtension getTesting();
}
