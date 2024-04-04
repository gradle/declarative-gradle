package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

/**
 * Creates a declarative {@link JavaApplication} DSL model, applies the official Java application plugin,
 * and links the declarative model to the official plugin.
 */
public class StandaloneJavaApplicationPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        JavaApplication dslModel = project.getExtensions().create("javaApplication", JavaApplication.class);

        project.getPlugins().apply(ApplicationPlugin.class);

        linkDslModelToPluginLazy(project, dslModel);
    }

    private void linkDslModelToPluginLazy(Project project, JavaApplication dslModel) {
        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
        java.getToolchain().getLanguageVersion().set(dslModel.getJavaVersion().map(JavaLanguageVersion::of));

        org.gradle.api.plugins.JavaApplication app = project.getExtensions().getByType(org.gradle.api.plugins.JavaApplication.class);
        app.getMainClass().set(dslModel.getMainClass());
    }
}
