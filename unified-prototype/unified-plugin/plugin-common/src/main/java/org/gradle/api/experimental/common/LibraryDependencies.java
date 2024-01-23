package org.gradle.api.experimental.common;

import org.gradle.api.artifacts.dsl.DependencyCollector;
import org.gradle.api.artifacts.dsl.GradleDependencies;
import org.gradle.api.plugins.jvm.PlatformDependencyModifiers;
import org.gradle.api.plugins.jvm.TestFixturesDependencyModifiers;

/**
 * The declarative dependencies DSL block for a library.
 */
public interface LibraryDependencies extends PlatformDependencyModifiers, TestFixturesDependencyModifiers, GradleDependencies {

    DependencyCollector getApi();
    DependencyCollector getImplementation();
    DependencyCollector getRuntimeOnly();
    DependencyCollector getCompileOnly();
    DependencyCollector getCompileOnlyApi();

}