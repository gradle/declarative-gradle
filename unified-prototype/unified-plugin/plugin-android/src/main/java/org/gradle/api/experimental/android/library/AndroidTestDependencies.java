package org.gradle.api.experimental.android.library;

import org.gradle.api.artifacts.dsl.Dependencies;
import org.gradle.api.artifacts.dsl.DependencyCollector;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@SuppressWarnings("UnstableApiUsage")
@Restricted
public interface AndroidTestDependencies extends Dependencies {
    DependencyCollector getTestImplementation();
    DependencyCollector getAndroidTestImplementation();
}
