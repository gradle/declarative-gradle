package org.gradle.api.experimental.android.internal;

import org.gradle.api.Project;
import org.gradle.api.experimental.android.AndroidLibrary;
import org.gradle.api.experimental.common.internal.AbstractAccessor;

import javax.inject.Inject;

/**
 * An extension registered to a project that makes the declarative Android library DSL accessible.
 */
public class AndroidLibraryAccessor extends AbstractAccessor<StandaloneAndroidPlugin, AndroidLibrary> {
    @Inject
    public AndroidLibraryAccessor(Project project) {
        super(project, StandaloneAndroidPlugin.class, AndroidLibrary.class);
    }
}