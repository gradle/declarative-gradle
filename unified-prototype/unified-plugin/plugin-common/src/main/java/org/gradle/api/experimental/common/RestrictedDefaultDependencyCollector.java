package org.gradle.api.experimental.common;

import org.gradle.api.internal.artifacts.dsl.dependencies.DefaultDependencyCollector;
import org.gradle.api.internal.artifacts.dsl.dependencies.DependencyFactoryInternal;

import javax.inject.Inject;

public abstract class RestrictedDefaultDependencyCollector extends DefaultDependencyCollector implements RestrictedDependencyCollector {
    @Inject
    public RestrictedDefaultDependencyCollector(DependencyFactoryInternal dependencyFactory) {
        super(dependencyFactory);
    }
}
