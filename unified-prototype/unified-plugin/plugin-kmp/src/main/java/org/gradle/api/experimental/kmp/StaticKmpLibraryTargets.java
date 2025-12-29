package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.declarative.dsl.model.annotations.Adding;

public interface StaticKmpLibraryTargets {
    @Adding
    void jvm();

    void jvm(Action<? super KmpLibraryJvmTarget> action);

    @Adding
    void nodeJs();

    void nodeJs(Action<? super KmpLibraryNodeJsTarget> action);

    @Adding
    void macOsArm64();

    void macOsArm64(Action<? super KmpLibraryNativeTarget> action);
}
