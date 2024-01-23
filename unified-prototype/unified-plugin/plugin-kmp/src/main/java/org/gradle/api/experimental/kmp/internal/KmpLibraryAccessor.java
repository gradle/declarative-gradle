package org.gradle.api.experimental.kmp.internal;

import org.gradle.api.Project;
import org.gradle.api.experimental.common.internal.AbstractAccessor;
import org.gradle.api.experimental.kmp.KmpLibrary;

import javax.inject.Inject;

/**
 * An extension registered to a project that makes the declarative KMP library DSL accessible.
 */
public class KmpLibraryAccessor extends AbstractAccessor<StandaloneKmpPlugin, KmpLibrary> {
    @Inject
    public KmpLibraryAccessor(Project project) {
        super(project, StandaloneKmpPlugin.class, KmpLibrary.class);
    }
}