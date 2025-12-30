package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;

public interface StaticKmpApplicationTargets {
    void jvm(Action<? super KmpApplicationJvmTarget> action);

    void nodeJs(Action<? super KmpApplicationNodeJsTarget> action);

    void macOsArm64(Action<? super KmpApplicationNativeTarget> action);
}
