package org.gradle.api.experimental.kmp.internal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.kmp.KmpLibrary;

/**
 * An internal plugin automatically applied by the {@link KmpLibraryAccessor} that
 * creates a declarative {@link KmpLibrary} DSL model, applies the official KMP plugin,
 * and links the declarative model to the official plugin.
 */
public class StandaloneKmpPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().create("__standalone_kmp_library", KmpLibrary.class);
        System.out.println("Applied KMP!");

        // TODO: Actually do something here
    }
}
