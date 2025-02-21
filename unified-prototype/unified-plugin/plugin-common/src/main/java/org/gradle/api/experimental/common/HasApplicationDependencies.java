package org.gradle.api.experimental.common;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;

/**
 * Something that has application dependencies.
 */
public interface HasApplicationDependencies {
    @Nested
    ApplicationDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super ApplicationDependencies> action) {
        action.execute(getDependencies());
    }
}
