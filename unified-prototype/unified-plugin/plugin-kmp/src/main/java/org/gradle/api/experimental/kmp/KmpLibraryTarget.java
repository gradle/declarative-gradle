package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.experimental.kmp.testing.TestingExtension;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;

public interface KmpLibraryTarget extends Named {
    @Nested
    LibraryDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super LibraryDependencies> action) {
        action.execute(getDependencies());
    }

    @Nested
    TestingExtension getTesting();

    @Configuring
    default void testing(Action<? super TestingExtension> action) {
        action.execute(getTesting());
    }
}
