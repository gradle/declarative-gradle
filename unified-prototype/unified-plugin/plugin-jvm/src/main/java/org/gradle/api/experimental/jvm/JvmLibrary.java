package org.gradle.api.experimental.jvm;

import org.gradle.api.experimental.common.HasLibraryDependencies;

/**
 * A library that runs on the JVM and that is implemented using one or more versions of Java.
 */
public interface JvmLibrary extends HasJavaTargets, HasLibraryDependencies {
}
