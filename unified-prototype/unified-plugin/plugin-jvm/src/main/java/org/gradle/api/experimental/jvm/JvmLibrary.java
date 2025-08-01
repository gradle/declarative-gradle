package org.gradle.api.experimental.jvm;

import org.gradle.api.experimental.common.HasLibraryDependencies;
import org.gradle.api.internal.plugins.HasBuildModel;

/**
 * A library that runs on the JVM and that is implemented using one or more versions of Java.
 */
public interface JvmLibrary extends HasJavaTargets, HasLibraryDependencies, HasBuildModel<JavaBuildModel> {
}
