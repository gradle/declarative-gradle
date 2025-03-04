package org.gradle.api.experimental.plugin;

import org.gradle.api.artifacts.dsl.Dependencies;
import org.gradle.api.artifacts.dsl.DependencyCollector;
import org.gradle.api.plugins.jvm.PlatformDependencyModifiers;

@SuppressWarnings("UnstableApiUsage")
public interface JavaGradlePluginDependencies extends Dependencies, PlatformDependencyModifiers {
    DependencyCollector getApi();
    DependencyCollector getImplementation();
}
