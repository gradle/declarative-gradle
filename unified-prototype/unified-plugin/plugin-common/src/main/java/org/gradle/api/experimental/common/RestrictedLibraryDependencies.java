package org.gradle.api.experimental.common;

import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public abstract class RestrictedLibraryDependencies implements LibraryDependencies {
    private final RestrictedDependencyCollector api;
    private final RestrictedDependencyCollector implementation;
    private final RestrictedDependencyCollector runtimeOnly;
    private final RestrictedDependencyCollector compileOnly;

    @Inject
    public RestrictedLibraryDependencies(ObjectFactory objectFactory) {
        this.api = objectFactory.newInstance(RestrictedDefaultDependencyCollector.class);
        this.implementation = objectFactory.newInstance(RestrictedDefaultDependencyCollector.class);
        this.runtimeOnly = objectFactory.newInstance(RestrictedDefaultDependencyCollector.class);
        this.compileOnly = objectFactory.newInstance(RestrictedDefaultDependencyCollector.class);
    }

    @Override
    public RestrictedDependencyCollector getApi() {
        return api;
    }

    @Override
    public RestrictedDependencyCollector getImplementation() {
        return implementation;
    }

    @Override
    public RestrictedDependencyCollector getRuntimeOnly() {
        return runtimeOnly;
    }

    @Override
    public RestrictedDependencyCollector getCompileOnly() {
        return compileOnly;
    }
}
