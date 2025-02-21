package org.gradle.api.experimental.jvm;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.experimental.jvm.extensions.testing.Testing;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;

public interface JvmTarget extends Named {

    @Nested
    LibraryDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super LibraryDependencies> action) {
        action.execute(getDependencies());
    }

    @Nested
    Testing getTesting();

    @Configuring
    default void testing(Action<? super Testing> action) {
        action.execute(getTesting());
    }
}
