package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.declarative.dsl.model.annotations.Adding;
import org.gradle.declarative.dsl.model.annotations.Configuring;

public interface StaticKmpLibraryTargets {
    @Adding
    void jvm();

    @Configuring
    void jvm(Action<? super KmpLibraryJvmTarget> action);

    @Adding
    void nodeJs();

    @Configuring
    void nodeJs(Action<? super KmpLibraryNodeJsTarget> action);

    @Adding
    void macOsArm64();

    @Configuring
    void macOsArm64(Action<? super KmpLibraryNativeTarget> action);
}
