package org.gradle.api.experimental.java;

import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.api.experimental.common.HasLibraryDependencies;
import org.gradle.api.experimental.jvm.JavaBuildModel;
import org.gradle.api.experimental.jvm.extensions.testing.Testing;
import org.gradle.api.internal.plugins.Definition;
import org.gradle.api.tasks.Nested;

/**
 * A library implemented using a single version of Java.
 */
public interface JavaLibrary extends HasJavaTarget, HasLibraryDependencies, Definition<JavaBuildModel> {
    @Nested
    Testing getTesting();
}
