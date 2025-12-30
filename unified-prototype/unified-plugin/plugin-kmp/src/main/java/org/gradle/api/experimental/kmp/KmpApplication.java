package org.gradle.api.experimental.kmp;

import org.gradle.api.experimental.common.HasApplicationDependencies;
import org.gradle.api.experimental.common.HasGroupAndVersion;
import org.gradle.api.internal.plugins.Definition;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;

/**
 * The public DSL interface for a declarative KMP application.
 */
public interface KmpApplication extends HasApplicationDependencies, HasGroupAndVersion, Definition<KotlinMultiplatformBuildModel> {
    @Input
    Property<String> getLanguageVersion();

    @Internal
    @Nested
    KmpApplicationTargetContainer getTargetsContainer();

    @Nested
    default StaticKmpApplicationTargets getTargets() {
        return getTargetsContainer();
    }
}
