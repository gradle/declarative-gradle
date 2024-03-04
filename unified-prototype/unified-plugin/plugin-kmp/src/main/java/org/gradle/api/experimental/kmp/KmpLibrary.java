package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Restricted;

import javax.inject.Inject;

/**
 * The public DSL interface for a declarative KMP library.
 */
public interface KmpLibrary {

    @Input
    Property<String> getLanguageVersion();

    @Nested
    LibraryDependencies getDependencies();

    default void dependencies(Action<? super LibraryDependencies> action) {
        action.execute(getDependencies());
    }

    KmpTargetContainer getTargets();

    default void targets(Action<? super KmpTargetContainer> action) {
        action.execute(getTargets());
    }
}
