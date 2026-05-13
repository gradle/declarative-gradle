package org.gradle.api.experimental.kmp;

import org.gradle.api.experimental.common.HasApplicationDependencies;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;

/**
 * The public DSL interface for a declarative KMP application.
 */
@SuppressWarnings("UnstableApiUsage")
public interface KmpApplication extends HasApplicationDependencies, HasKmpDefinition<KotlinMultiplatformBuildModel> {
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
