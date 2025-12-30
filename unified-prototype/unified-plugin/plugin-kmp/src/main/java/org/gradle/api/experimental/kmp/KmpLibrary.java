package org.gradle.api.experimental.kmp;

import org.gradle.api.experimental.common.HasGroupAndVersion;
import org.gradle.api.experimental.common.HasLibraryDependencies;
import org.gradle.api.internal.plugins.Definition;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;

/**
 * The public DSL interface for a declarative KMP library.
 */
public interface KmpLibrary extends HasLibraryDependencies, HasGroupAndVersion, Definition<KotlinMultiplatformBuildModel> {
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
