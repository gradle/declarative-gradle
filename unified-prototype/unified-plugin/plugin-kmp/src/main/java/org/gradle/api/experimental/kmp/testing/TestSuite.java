package org.gradle.api.experimental.kmp.testing;

import org.gradle.api.Action;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * Represents a custom suite of tests for a KMP project.
 */
public interface TestSuite {
    @Restricted
    DirectoryProperty getSourceRoot();

    @Nested
    TestingDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super TestingDependencies> action) {
        action.execute(getDependencies());
    }
}
