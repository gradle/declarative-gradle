package org.gradle.api.experimental.kotlin;

import org.gradle.api.Action;
import org.gradle.api.experimental.common.extensions.HasLinting;
import org.gradle.api.experimental.common.extensions.Lint;
import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.api.experimental.common.HasLibraryDependencies;
import org.gradle.api.experimental.kotlin.testing.Testing;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * A library implemented using Kotlin and that targets a single JVM version.
 */
@Restricted
public interface KotlinJvmLibrary extends HasJavaTarget, HasLibraryDependencies, HasLinting {
    @Override
    @Nested
    Lint getLint();

    @Configuring
    default void lint(Action<? super Lint> action) {
        action.execute(getLint());
    }

    @Nested
    Testing getTesting();

    @Configuring
    default void testing(Action<? super Testing> action) {
        action.execute(getTesting());
    }
}
