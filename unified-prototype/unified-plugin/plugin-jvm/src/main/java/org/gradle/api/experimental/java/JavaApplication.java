package org.gradle.api.experimental.java;

import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.api.experimental.jvm.HasJvmApplication;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * An application implemented using a single version of Java.
 */
@Restricted
public interface JavaApplication extends HasJavaTarget, HasJvmApplication {
}
