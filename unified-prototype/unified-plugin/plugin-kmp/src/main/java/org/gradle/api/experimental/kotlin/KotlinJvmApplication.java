package org.gradle.api.experimental.kotlin;

import org.gradle.api.Action;
import org.gradle.api.experimental.common.HasCliExecutables;
import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.api.experimental.jvm.HasJvmApplication;
import org.gradle.api.experimental.kotlin.testing.Testing;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * An application implemented using Kotlin and that targets a single JVM version.
 */
@Restricted
public interface KotlinJvmApplication extends HasJavaTarget, HasJvmApplication, HasCliExecutables {
    @Nested
    Testing getTesting();

    @Configuring
    default void testing(Action<? super Testing> action) {
        action.execute(getTesting());
    }
}
