package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.experimental.common.HasLibraryDependencies;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * The public DSL interface for a declarative KMP library.
 */
@Restricted
public interface KmpLibrary extends HasLibraryDependencies {
    @Input
    Property<String> getLanguageVersion();

    @Internal
    @Nested
    KmpLibraryTargetContainer getTargetsContainer();

    @Nested
    default StaticKmpLibraryTargets getTargets() {
        return getTargetsContainer();
    }

    @Configuring
    default void targets(Action<? super StaticKmpLibraryTargets> action) {
        action.execute(getTargets());
    }
}
