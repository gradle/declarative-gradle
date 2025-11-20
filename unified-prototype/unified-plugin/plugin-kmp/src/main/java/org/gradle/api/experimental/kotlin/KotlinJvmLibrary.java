package org.gradle.api.experimental.kotlin;

import org.gradle.api.Action;
import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.api.experimental.common.HasLibraryDependencies;
import org.gradle.api.experimental.kotlin.testing.Testing;
import org.gradle.api.internal.plugins.Definition;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;

/**
 * A library implemented using Kotlin and that targets a single JVM version.
 */
public interface KotlinJvmLibrary extends HasJavaTarget, HasLibraryDependencies, Definition<KotlinJvmLibraryBuildModel> {
    @Nested
    Testing getTesting();

    @Configuring
    default void testing(Action<? super Testing> action) {
        action.execute(getTesting());
    }
}
