package orggradle.experiments;

import org.gradle.api.artifacts.dsl.Dependencies;
import org.gradle.api.plugins.jvm.PlatformDependencyModifiers;
import org.gradle.api.plugins.jvm.TestFixturesDependencyModifiers;
import org.gradle.api.tasks.Nested;

public interface JvmLibraryDependencies extends PlatformDependencyModifiers, TestFixturesDependencyModifiers, Dependencies {
    @Nested
    DependencyCollector getApi();

    @Nested
    DependencyCollector getCompileOnlyApi();

    @Nested
    DependencyCollector getImplementation();

    @Nested
    DependencyCollector getCompileOnly();

    @Nested
    DependencyCollector getRuntimeOnly();
}
