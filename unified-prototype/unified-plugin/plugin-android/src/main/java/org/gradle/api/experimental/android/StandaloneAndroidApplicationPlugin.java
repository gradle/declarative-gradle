package org.gradle.api.experimental.android;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Creates a declarative {@link StandaloneAndroidLibrary} DSL model, applies the official Android plugin,
 * and links the declarative model to the official plugin.
 */
public abstract class StandaloneAndroidApplicationPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        createDslModel(project);
    }

    private StandaloneAndroidApplication createDslModel(Project project) {
        AndroidTarget dslDebug = project.getObjects().newInstance(AndroidTarget.class, "debug");
        AndroidTarget dslRelease = project.getObjects().newInstance(AndroidTarget.class, "release");
        AndroidTargets dslTargets = project.getExtensions().create("targets", AndroidTargets.class, dslDebug, dslRelease);
        return project.getExtensions().create("androidApplication", StandaloneAndroidApplication.class, dslTargets);
    }
}
