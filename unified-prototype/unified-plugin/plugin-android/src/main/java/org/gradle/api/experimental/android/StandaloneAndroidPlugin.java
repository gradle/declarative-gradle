package org.gradle.api.experimental.android;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Creates a declarative {@link AndroidLibrary} DSL model, applies the official Android plugin,
 * and links the declarative model to the official plugin.
 */
public class StandaloneAndroidPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().create("androidLibrary", AndroidLibrary.class);
        System.out.println("Applied Android!");

        // TODO: Actually do something here
    }
}
