package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.experimental.common.HasGroupAndVersion;
import org.gradle.api.experimental.common.HasLibraryDependencies;
import org.gradle.api.internal.plugins.HasBuildModel;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;

/**
 * The public DSL interface for a declarative KMP library.
 */
public interface KmpLibrary extends HasLibraryDependencies, HasGroupAndVersion, HasBuildModel<KotlinMultiplatformBuildModel> {
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
