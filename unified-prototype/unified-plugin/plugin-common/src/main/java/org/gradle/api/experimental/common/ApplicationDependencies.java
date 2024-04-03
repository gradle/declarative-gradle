package org.gradle.api.experimental.common;

import org.gradle.api.artifacts.dsl.DependencyCollector;
import org.gradle.api.artifacts.dsl.GradleDependencies;
import org.gradle.api.plugins.jvm.PlatformDependencyModifiers;
import org.gradle.api.plugins.jvm.TestFixturesDependencyModifiers;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * The declarative dependencies DSL block for an application.
 */
@SuppressWarnings("UnstableApiUsage")
@Restricted
public interface ApplicationDependencies extends PlatformDependencyModifiers, TestFixturesDependencyModifiers, GradleDependencies {
    DependencyCollector getImplementation();
    DependencyCollector getRuntimeOnly();
    DependencyCollector getCompileOnly();
}
