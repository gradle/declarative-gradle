package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.experimental.common.ApplicationDependencies;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface KmpApplicationTarget extends Named {
    @Nested
    ApplicationDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super ApplicationDependencies> action) {
        action.execute(getDependencies());
    }
}
