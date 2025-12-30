package org.gradle.api.experimental.jvm.extensions.testing;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

public interface Testing {
    Property<Integer> getTestJavaVersion();

    @Nested
    TestDependencies getDependencies();
}
