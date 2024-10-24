package org.gradle.api.experimental.kotlin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.common.CliApplicationConventionsPlugin;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.experimental.kmp.internal.KotlinPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.plugins.ApplicationPlugin;

/**
 * Creates a declarative {@link KotlinJvmApplication} DSL model, applies the official Kotlin and application plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class StandaloneKotlinJvmApplicationPlugin implements Plugin<Project> {

    public static final String KOTLIN_JVM_APPLICATION = "kotlinJvmApplication";

    @SoftwareType(name = KOTLIN_JVM_APPLICATION, modelPublicType = KotlinJvmApplication.class)
    public abstract KotlinJvmApplication getApplication();

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
        configureTesting(project, dslModel);

        dslModel.getRunTasks().add(project.getTasks().named("run"));
    }

    private void configureTesting(Project project, KotlinJvmApplication dslModel) {
        ConfigurationContainer configurations = project.getConfigurations();
        configurations.getByName("testImplementation").fromDependencyCollector(dslModel.getTesting().getDependencies().getImplementation());
        configurations.getByName("testCompileOnly").fromDependencyCollector(dslModel.getTesting().getDependencies().getCompileOnly());
        configurations.getByName("testRuntimeOnly").fromDependencyCollector(dslModel.getTesting().getDependencies().getRuntimeOnly());
    }
}
