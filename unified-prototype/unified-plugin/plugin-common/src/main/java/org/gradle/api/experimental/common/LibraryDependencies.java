package org.gradle.api.experimental.common;

import org.gradle.api.artifacts.dsl.DependencyCollector;

/**
 * The declarative dependencies DSL block for a library.
 */
@SuppressWarnings("UnstableApiUsage")
public interface LibraryDependencies extends BasicDependencies {
    DependencyCollector getApi();

    // CompileOnlyApi is not included here, since both Android and KMP do not support it.
    // Does that mean we should also reconsider if we should support it? Or, should we
    // talk to Android and KMP about adding support
}
