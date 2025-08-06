package org.gradle.api.experimental.android.extensions.testing;

import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface Testing {
    /**
     * Whether to set up Jacoco support.
     */
    @Nested
    Jacoco getJacoco();

    @Configuring
    default void jacoco(Action<? super Jacoco> action) {
        action.execute(getJacoco());
    }

    @Nested
    Roborazzi getRoborazzi();

    @Configuring
    default void roborazzi(Action<? super Roborazzi> action) {
        action.execute(getRoborazzi());
    }

    @Nested
    TestOptions getTestOptions();

    @Configuring
    default void testOptions(Action<? super TestOptions> action) {
        action.execute(getTestOptions());
    }

    @Nested
    AndroidTestDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super AndroidTestDependencies> action) {
        action.execute(getDependencies());
    }

    @Restricted
    Property<Boolean> getFailOnNoDiscoveredTests();
}
