package org.gradle.api.experimental.kotlin;

import org.gradle.api.Action;
import org.gradle.api.experimental.common.HasCliExecutables;
import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.api.experimental.jvm.HasJvmApplication;
import org.gradle.api.experimental.kotlin.testing.Testing;
import org.gradle.api.internal.plugins.HasBuildModel;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.jspecify.annotations.NonNull;

/**
 * An application implemented using Kotlin and that targets a single JVM version.
 */
public interface KotlinJvmApplication extends HasJavaTarget, HasJvmApplication, HasCliExecutables, HasBuildModel<@NonNull KotlinJvmApplicationBuildModel> {
    @Nested
    Testing getTesting();

    @Configuring
    default void testing(Action<? super Testing> action) {
        action.execute(getTesting());
    }
}
