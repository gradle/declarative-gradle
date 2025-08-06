package org.gradle.api.experimental.java;

import org.gradle.api.Action;
import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.api.experimental.common.HasLibraryDependencies;
import org.gradle.api.experimental.jvm.JavaBuildModel;
import org.gradle.api.experimental.jvm.extensions.testing.Testing;
import org.gradle.api.internal.plugins.HasBuildModel;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.jspecify.annotations.NonNull;

/**
 * A library implemented using a single version of Java.
 */
public interface JavaLibrary extends HasJavaTarget, HasLibraryDependencies, HasBuildModel<@NonNull JavaBuildModel> {
    @Nested
    Testing getTesting();

    @Configuring
    default void testing(Action<? super Testing> action) {
        action.execute(getTesting());
    }
}
