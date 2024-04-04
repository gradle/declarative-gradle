package org.gradle.api.experimental.common;

import org.gradle.api.artifacts.dsl.Dependencies;
import org.gradle.api.artifacts.dsl.DependencyCollector;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * The declarative dependencies DSL block for a library.
 */
@SuppressWarnings("UnstableApiUsage")
@Restricted
public interface LibraryDependencies extends Dependencies {
    DependencyCollector getApi();
    DependencyCollector getImplementation();
    DependencyCollector getRuntimeOnly();
    DependencyCollector getCompileOnly();

    // CompileOnlyApi is not included here, since both Android and KMP do not support it.
    // Does that mean we should also reconsider if we should support it? Or, should we
    // talk to Android and KMP about adding support
}
