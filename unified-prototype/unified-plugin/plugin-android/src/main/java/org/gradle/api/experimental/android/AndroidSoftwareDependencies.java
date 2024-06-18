package org.gradle.api.experimental.android;

import org.gradle.api.artifacts.dsl.Dependencies;
import org.gradle.api.artifacts.dsl.DependencyCollector;

@SuppressWarnings("UnstableApiUsage")
public interface AndroidSoftwareDependencies extends Dependencies {
    DependencyCollector getImplementation();
    DependencyCollector getRuntimeOnly();
    DependencyCollector getCompileOnly();
}
