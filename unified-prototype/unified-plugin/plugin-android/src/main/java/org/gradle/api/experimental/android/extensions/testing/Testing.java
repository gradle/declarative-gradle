package org.gradle.api.experimental.android.extensions.testing;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

public interface Testing {
    /**
     * Whether to set up Jacoco support.
     */
    @Nested
    Jacoco getJacoco();

    @Nested
    Roborazzi getRoborazzi();

    @Nested
    TestOptions getTestOptions();

    @Nested
    AndroidTestDependencies getDependencies();

    Property<Boolean> getFailOnNoDiscoveredTests();
}
