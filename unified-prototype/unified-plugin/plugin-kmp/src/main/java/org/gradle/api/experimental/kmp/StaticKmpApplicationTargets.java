package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.declarative.dsl.model.annotations.Adding;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface StaticKmpApplicationTargets {
    @Adding
    void jvm();

    @Configuring
    void jvm(Action<? super KmpApplicationJvmTarget> action);

    @Adding
    void nodeJs();

    @Configuring
    void nodeJs(Action<? super KmpApplicationNodeJsTarget> action);

    @Adding
    void macOsArm64();

    @Configuring
    void macOsArm64(Action<? super KmpApplicationNativeTarget> action);
}
