package org.gradle.api.experimental.jvm;

import org.gradle.api.experimental.common.HasCliExecutables;
import org.gradle.api.internal.plugins.HasBuildModel;
import org.jspecify.annotations.NonNull;

/**
 * An application that runs on the JVM and that is implemented using one or more versions of Java.
 */
public interface JvmApplication extends HasJavaTargets, HasJvmApplication, HasCliExecutables, HasBuildModel<@NonNull JavaApplicationBuildModel> {
}
