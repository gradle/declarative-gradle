package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.experimental.common.HasApplicationDependencies;
import org.gradle.api.experimental.common.HasCliExecutables;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * The public DSL interface for a declarative KMP application.
 */
@Restricted
public interface KmpApplication extends HasApplicationDependencies, HasCliExecutables {
    @Input
    Property<String> getLanguageVersion();

    @Internal
    @Nested
    KmpApplicationTargetContainer getTargetsContainer();

    @Nested
    default StaticKmpApplicationTargets getTargets() {
        return getTargetsContainer();
    }

    @Configuring
    default void targets(Action<? super StaticKmpApplicationTargets> action) {
        action.execute(getTargets());
    }
}
