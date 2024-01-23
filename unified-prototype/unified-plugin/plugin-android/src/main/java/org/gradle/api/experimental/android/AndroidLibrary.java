package org.gradle.api.experimental.android;

import org.gradle.api.Action;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.tasks.Nested;

/**
 * The public DSL interface for a declarative Android library.
 */
public interface AndroidLibrary {

    @Nested
    LibraryDependencies getDependencies();

    default void dependencies(Action<? super LibraryDependencies> action) {
        action.execute(getDependencies());
    }

}
