package org.gradle.api.experimental.jvm;

import org.gradle.api.experimental.common.HasLibraryDependencies;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * A library that runs on the JVM and that is implemented using one or more versions of Java.
 */
@Restricted
public interface JvmLibrary extends HasJavaTargets, HasLibraryDependencies {
}
