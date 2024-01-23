package org.gradle.api.experimental.jvm.internal;

import org.gradle.api.Project;
import org.gradle.api.experimental.common.internal.AbstractAccessor;
import org.gradle.api.experimental.jvm.JvmLibrary;

import javax.inject.Inject;

/**
 * An extension registered to a project that makes the declarative JVM library DSL accessible.
 */
public class JvmLibraryAccessor extends AbstractAccessor<StandaloneJvmPlugin, JvmLibrary> {
    @Inject
    public JvmLibraryAccessor(Project project) {
        super(project, StandaloneJvmPlugin.class, JvmLibrary.class);
    }
}