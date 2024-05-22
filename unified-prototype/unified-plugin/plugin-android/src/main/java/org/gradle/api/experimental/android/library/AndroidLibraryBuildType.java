package org.gradle.api.experimental.android.library;

import org.gradle.api.Action;
import org.gradle.api.experimental.android.extensions.Minify;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface AndroidLibraryBuildType {
    /**
     * Dependencies for this build type.
     */
    @Nested
    AndroidLibraryDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super AndroidLibraryDependencies> action) {
        action.execute(getDependencies());
    }

    @Nested
    Minify getMinify();

    @Configuring
    default void minify(Action<? super Minify> action) {
        action.execute(getMinify());
    }
}
