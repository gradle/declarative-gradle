package org.gradle.api.experimental.jvm;

import org.gradle.api.Action;
import org.gradle.api.experimental.common.ApplicationDependencies;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface JvmApplication extends HasJavaTargets {
    @Restricted
    Property<String> getMainClass();

    @Nested
    ApplicationDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super ApplicationDependencies> action) {
        action.execute(getDependencies());
    }
}
