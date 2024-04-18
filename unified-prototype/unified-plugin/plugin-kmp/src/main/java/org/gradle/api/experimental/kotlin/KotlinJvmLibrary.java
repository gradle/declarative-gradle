package org.gradle.api.experimental.kotlin;

import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.api.experimental.jvm.HasJvmLibrary;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * A library implemented using Kotlin and that targets a single JVM version.
 */
@Restricted
public interface KotlinJvmLibrary extends HasJavaTarget, HasJvmLibrary {
}
