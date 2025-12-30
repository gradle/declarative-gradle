package org.gradle.api.experimental.kmp;

import org.gradle.api.Named;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.experimental.kmp.testing.TestingExtension;
import org.gradle.api.tasks.Nested;

public interface KmpLibraryTarget extends Named {
    @Nested
    LibraryDependencies getDependencies();

    @Nested
    TestingExtension getTesting();
}
