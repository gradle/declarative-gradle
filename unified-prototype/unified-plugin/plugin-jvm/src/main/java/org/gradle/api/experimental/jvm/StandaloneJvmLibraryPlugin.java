package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Creates a declarative {@link JvmLibrary} DSL model, applies the official Jvm plugin,
 * and links the declarative model to the official plugin.
 */
public class StandaloneJvmLibraryPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().create("jvmLibrary", JvmLibrary.class);
        System.out.println("Applied JVM!");

        // TODO: Actually do something here
    }
}
