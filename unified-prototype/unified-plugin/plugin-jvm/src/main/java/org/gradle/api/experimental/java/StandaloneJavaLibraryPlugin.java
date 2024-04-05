package org.gradle.api.experimental.java;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

/**
 * Creates a declarative {@link JavaLibrary} DSL model, applies the official Java library plugin,
 * and links the declarative model to the official plugin.
 */
public abstract class StandaloneJavaLibraryPlugin implements Plugin<Project> {
    @SoftwareType(name = "javaLibrary", modelPublicType = JavaLibrary.class)
    abstract public JavaLibrary getJavaApplication();

    @Override
    public void apply(Project project) {
        JavaLibrary dslModel = getJavaApplication();

        project.getPlugins().apply(JavaLibraryPlugin.class);

        linkDslModelToPluginLazy(project, dslModel);
    }

    private void linkDslModelToPluginLazy(Project project, JavaLibrary dslModel) {
        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
        java.getToolchain().getLanguageVersion().set(dslModel.getJavaVersion().map(JavaLanguageVersion::of));

        JvmPluginSupport.linkSourceSetToDependencies(project, java.getSourceSets().getByName("main"), dslModel.getDependencies());
    }
}
