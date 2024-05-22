package org.gradle.api.experimental.java;

import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.api.experimental.common.HasLibraryDependencies;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * A library implemented using a single version of Java.
 */
@Restricted
public interface JavaLibrary extends HasJavaTarget, HasLibraryDependencies {
}
