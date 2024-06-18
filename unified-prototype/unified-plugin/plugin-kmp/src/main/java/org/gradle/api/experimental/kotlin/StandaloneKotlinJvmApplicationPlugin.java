package org.gradle.api.experimental.kotlin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.common.CliApplicationConventionsPlugin;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.experimental.kmp.internal.KotlinPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.plugins.ApplicationPlugin;

/**
 * Creates a declarative {@link KotlinJvmApplication} DSL model, applies the official Kotlin and application plugin,
 * and links the declarative model to the official plugin.
 */
abstract public class StandaloneKotlinJvmApplicationPlugin implements Plugin<Project> {
    @SoftwareType(name = "kotlinJvmApplication", modelPublicType = KotlinJvmApplication.class)
    abstract public KotlinJvmApplication getApplication();

    @Override
    public void apply(Project project) {
        KotlinJvmApplication dslModel = getApplication();

        project.getPlugins().apply(ApplicationPlugin.class);
        project.getPlugins().apply("org.jetbrains.kotlin.jvm");
        project.getPlugins().apply(CliApplicationConventionsPlugin.class);

        linkDslModelToPlugin(project, dslModel);
    }

    private void linkDslModelToPlugin(Project project, KotlinJvmApplication dslModel) {
        KotlinPluginSupport.linkJavaVersion(project, dslModel);
        JvmPluginSupport.linkApplicationMainClass(project, dslModel);
        JvmPluginSupport.linkMainSourceSourceSetDependencies(project, dslModel.getDependencies());

        dslModel.getRunTasks().add(project.getTasks().named("run"));
    }
}
