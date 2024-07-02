package org.gradle.api.experimental.jvm.extensions.testing;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface Testing {
    @Nested
    JvmTestDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super JvmTestDependencies> action) {
        action.execute(getDependencies());
    }
}
