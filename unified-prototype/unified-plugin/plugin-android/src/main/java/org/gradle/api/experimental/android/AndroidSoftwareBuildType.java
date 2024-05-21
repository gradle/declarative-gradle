package org.gradle.api.experimental.android;

import org.gradle.api.Action;
import org.gradle.api.experimental.android.extensions.Minify;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface AndroidSoftwareBuildType {
    /**
     * Dependencies for this build type.
     */
    AndroidSoftwareDependencies getDependencies();

    @Nested
    Minify getMinify();

    @Configuring
    default void minify(Action<? super Minify> action) {
        action.execute(getMinify());
    }
}
