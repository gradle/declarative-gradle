package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.experimental.common.ApplicationDependencies;
import org.gradle.api.experimental.kmp.testing.TestingExtension;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;

/**
 * Represents a target platform in a KMP application.
 */
public interface KmpApplicationTarget extends Named {
    @Nested
    ApplicationDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super ApplicationDependencies> action) {
        action.execute(getDependencies());
    }

    @Nested
    TestingExtension getTesting();

    @Configuring
    default void testing(Action<? super TestingExtension> action) {
        action.execute(getTesting());
    }
}
