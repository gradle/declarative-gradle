package org.gradle.api.experimental.android;

import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface AndroidSoftwareBuildTypes {
    @Nested
    AndroidSoftwareBuildType getDebug();

    @Nested
    AndroidSoftwareBuildType getRelease();
}
