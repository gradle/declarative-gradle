package org.gradle.api.experimental.kotlin;

import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.api.experimental.jvm.HasJvmApplication;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * An application implemented using Kotlin and that targets a single JVM version.
 */
@Restricted
public interface KotlinJvmApplication extends HasJavaTarget, HasJvmApplication {
}
