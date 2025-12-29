package org.gradle.api.experimental.jvm;

import org.gradle.api.tasks.Nested;

/**
 * A component that is built for multiple Java versions.
 */
public interface HasJavaTargets {

    @Nested
    JvmTargetContainer getTargets();
}
