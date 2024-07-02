package org.gradle.api.experimental.jvm.extensions.testing;

import org.gradle.api.artifacts.dsl.Dependencies;
import org.gradle.api.artifacts.dsl.DependencyCollector;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@SuppressWarnings("UnstableApiUsage")
@Restricted
public interface JvmTestDependencies extends Dependencies {
    DependencyCollector getImplementation();
    DependencyCollector getCompileOnly();
    DependencyCollector getRuntimeOnly();
}
