package org.gradle.api.experimental.android.internal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.android.AndroidLibrary;

/**
 * An internal plugin automatically applied by the {@link AndroidLibraryAccessor} that
 * creates a declarative {@link AndroidLibrary} DSL model, applies the official Android plugin,
 * and links the declarative model to the official plugin.
 */
public class StandaloneAndroidPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().create("__standalone_android_library", AndroidLibrary.class);
        System.out.println("Applied Android!");

        // TODO: Actually do something here
    }
}
