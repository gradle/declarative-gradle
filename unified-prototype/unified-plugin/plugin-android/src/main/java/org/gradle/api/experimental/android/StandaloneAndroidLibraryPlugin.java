package org.gradle.api.experimental.android;

import org.gradle.api.*;

/**
 * Creates a declarative {@link StandaloneAndroidLibrary} DSL model, applies the official Android plugin,
 * and links the declarative model to the official plugin.
 */
public abstract class StandaloneAndroidLibraryPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        StandaloneAndroidLibrary dslModel = createDslModel(project);

        // Register an afterEvaluate listener before we apply the Android plugin to ensure we can
        // run actions before Android does.
        project.afterEvaluate(p -> AndroidDSLSupport.linkDslModelToPlugin(p, dslModel));

        // Apply the official Android plugin.
        project.getPlugins().apply("com.android.library");
        project.getPlugins().apply("org.jetbrains.kotlin.android");

        AndroidDSLSupport.linkDslModelToPluginLazy(project, dslModel);
    }

    private StandaloneAndroidLibrary createDslModel(Project project) {
        AndroidTarget dslDebug = project.getObjects().newInstance(AndroidTarget.class, "debug");
        AndroidTarget dslRelease = project.getObjects().newInstance(AndroidTarget.class, "release");
        AndroidTargets dslTargets = project.getExtensions().create("targets", AndroidTargets.class, dslDebug, dslRelease);
        return project.getExtensions().create("androidLibrary", StandaloneAndroidLibrary.class, dslTargets);
    }
}
