package org.gradle.api.experimental.jvm;

import org.gradle.api.Named;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.experimental.jvm.extensions.testing.Testing;
import org.gradle.api.tasks.Nested;

public interface JvmTarget extends Named {

    @Nested
    LibraryDependencies getDependencies();

    @Nested
    Testing getTesting();
}
