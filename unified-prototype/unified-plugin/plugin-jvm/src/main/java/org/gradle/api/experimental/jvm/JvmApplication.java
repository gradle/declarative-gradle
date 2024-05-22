package org.gradle.api.experimental.jvm;

import org.gradle.api.experimental.common.HasCliExecutables;

/**
 * An application that runs on the JVM and that is implemented using one or more versions of Java.
 */
public interface JvmApplication extends HasJavaTargets, HasJvmApplication, HasCliExecutables {
}
