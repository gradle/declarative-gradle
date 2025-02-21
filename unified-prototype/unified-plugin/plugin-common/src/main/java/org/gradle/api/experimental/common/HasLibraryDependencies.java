package org.gradle.api.experimental.common;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;

/**
 * Something that has library dependencies.
 */
public interface HasLibraryDependencies {
    @Nested
    LibraryDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super LibraryDependencies> action) {
        action.execute(getDependencies());
    }
}
