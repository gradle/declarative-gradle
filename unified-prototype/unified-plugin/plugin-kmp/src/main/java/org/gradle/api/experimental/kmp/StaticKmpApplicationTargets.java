package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.declarative.dsl.model.annotations.Adding;

public interface StaticKmpApplicationTargets {
    @Adding
    void jvm();

    void jvm(Action<? super KmpApplicationJvmTarget> action);

    @Adding
    void nodeJs();

    void nodeJs(Action<? super KmpApplicationNodeJsTarget> action);

    @Adding
    void macOsArm64();

    void macOsArm64(Action<? super KmpApplicationNativeTarget> action);
}
