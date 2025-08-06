package org.gradle.api.experimental.java;

import org.gradle.api.Action;
import org.gradle.api.experimental.common.HasCliExecutables;
import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.api.experimental.jvm.HasJvmApplication;
import org.gradle.api.experimental.jvm.JavaApplicationBuildModel;
import org.gradle.api.experimental.jvm.extensions.testing.Testing;
import org.gradle.api.internal.plugins.HasBuildModel;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.jspecify.annotations.NonNull;

/**
 * An application implemented using a single version of Java.
 */
public interface JavaApplication extends HasJavaTarget, HasJvmApplication, HasCliExecutables, HasBuildModel<@NonNull JavaApplicationBuildModel> {
    @Nested
    Testing getTesting();

    @Configuring
    default void testing(Action<? super Testing> action) {
        action.execute(getTesting());
    }
}
