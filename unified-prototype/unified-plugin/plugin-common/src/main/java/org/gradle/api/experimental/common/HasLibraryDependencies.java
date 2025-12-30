package org.gradle.api.experimental.common;

import org.gradle.api.tasks.Nested;

/**
 * Something that has library dependencies.
 */
public interface HasLibraryDependencies {
    @Nested
    LibraryDependencies getDependencies();
}
