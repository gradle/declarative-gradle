package org.gradle.api.experimental.kmp;

import org.gradle.api.experimental.common.HasLibraryDependencies;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;

/**
 * The public DSL interface for a declarative KMP library.
 */
@SuppressWarnings("UnstableApiUsage")
public interface KmpLibrary extends HasLibraryDependencies, HasKmpDefinition<KotlinMultiplatformBuildModel> {
    @Input
    Property<String> getLanguageVersion();

    @Internal
    @Nested
    KmpLibraryTargetContainer getTargetsContainer();

    @Nested
    default StaticKmpLibraryTargets getTargets() {
        return getTargetsContainer();
    }
}
