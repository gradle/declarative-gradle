package org.gradle.api.experimental.common;

import org.gradle.api.tasks.Nested;

/**
 * Something that has application dependencies.
 */
public interface HasApplicationDependencies {
    @Nested
    ApplicationDependencies getDependencies();
}
