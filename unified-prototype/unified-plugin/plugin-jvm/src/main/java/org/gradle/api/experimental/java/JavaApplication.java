package org.gradle.api.experimental.java;

import org.gradle.api.Action;
import org.gradle.api.experimental.common.HasCliExecutables;
import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.api.experimental.jvm.HasJvmApplication;
import org.gradle.api.experimental.jvm.extensions.testing.Testing;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * An application implemented using a single version of Java.
 */
@Restricted
public interface JavaApplication extends HasJavaTarget, HasJvmApplication, HasCliExecutables {
    @Nested
    Testing getTesting();

    @Configuring
    default void testing(Action<? super Testing> action) {
        action.execute(getTesting());
    }
}
