package org.gradle.api.experimental.android;

import org.gradle.api.artifacts.dsl.Dependencies;
import org.gradle.api.artifacts.dsl.DependencyCollector;

@SuppressWarnings("UnstableApiUsage")
public interface AndroidSoftwareDependencies extends Dependencies {
    DependencyCollector getImplementation();
    DependencyCollector getRuntimeOnly();
    DependencyCollector getCompileOnly();

    // Added by Android Gradle Plugin https://googlesamples.github.io/android-custom-lint-rules/api-guide/publishing.md.html
    DependencyCollector getLintChecks();
    DependencyCollector getLintPublish();
}
