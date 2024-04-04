package org.gradle.api.experimental.common;

import org.gradle.api.artifacts.dsl.Dependencies;
import org.gradle.api.artifacts.dsl.DependencyCollector;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * The declarative dependencies DSL block for an application.
 */
@SuppressWarnings("UnstableApiUsage")
@Restricted
public interface ApplicationDependencies extends Dependencies {
    DependencyCollector getImplementation();
    DependencyCollector getRuntimeOnly();
    DependencyCollector getCompileOnly();
}
