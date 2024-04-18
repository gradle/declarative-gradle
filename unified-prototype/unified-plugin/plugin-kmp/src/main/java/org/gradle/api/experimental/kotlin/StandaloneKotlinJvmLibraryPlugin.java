package org.gradle.api.experimental.kotlin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.experimental.kmp.internal.KotlinPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;

/**
 * Creates a declarative {@link KotlinJvmApplication} DSL model, applies the official Kotlin and application plugin,
 * and links the declarative model to the official plugin.
 */
abstract public class StandaloneKotlinJvmLibraryPlugin implements Plugin<Project> {
    @SoftwareType(name = "kotlinJvmLibrary", modelPublicType = KotlinJvmLibrary.class)
    abstract public KotlinJvmLibrary getLibrary();

    @Override
    public void apply(Project project) {
        KotlinJvmLibrary dslModel = getLibrary();

        project.getPlugins().apply("org.jetbrains.kotlin.jvm");

        linkDslModelToPlugin(project, dslModel);
    }

    private void linkDslModelToPlugin(Project project, KotlinJvmLibrary dslModel) {
        KotlinPluginSupport.linkJavaVersion(project, dslModel);
        JvmPluginSupport.linkMainSourceSourceSetDependencies(project, dslModel.getDependencies());
    }
}
