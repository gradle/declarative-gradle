package org.gradle.api.experimental.kmp;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Creates a declarative {@link KmpLibrary} DSL model, applies the official KMP plugin,
 * and links the declarative model to the official plugin.
 */
public class StandaloneKmpPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().create("kmpLibrary", KmpLibrary.class);
        System.out.println("Applied KMP!");

        // TODO: Actually do something here
    }
}
