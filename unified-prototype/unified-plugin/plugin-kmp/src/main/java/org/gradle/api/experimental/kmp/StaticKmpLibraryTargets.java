package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;

public interface StaticKmpLibraryTargets {
    void jvm(Action<? super KmpLibraryJvmTarget> action);

    void nodeJs(Action<? super KmpLibraryNodeJsTarget> action);

    void macOsArm64(Action<? super KmpLibraryNativeTarget> action);
}
