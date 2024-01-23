package org.gradle.api.experimental.jvm.internal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.jvm.JvmLibrary;

/**
 * An internal plugin automatically applied by the {@link JvmLibraryAccessor} that
 * creates a declarative {@link JvmLibrary} DSL model, applies the official Jvm plugin,
 * and links the declarative model to the official plugin.
 */
public class StandaloneJvmPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().create("__standalone_jvm_library", JvmLibrary.class);
        System.out.println("Applied JVM!");

        // TODO: Actually do something here
    }
}
