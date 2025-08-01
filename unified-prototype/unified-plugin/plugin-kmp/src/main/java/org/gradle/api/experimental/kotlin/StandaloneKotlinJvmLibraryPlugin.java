package org.gradle.api.experimental.kotlin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.experimental.kmp.internal.KotlinPluginSupport;
import org.gradle.api.internal.plugins.BindsSoftwareType;
import org.gradle.api.internal.plugins.SoftwareTypeBindingBuilder;
import org.gradle.api.internal.plugins.SoftwareTypeBindingRegistration;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.testing.Test;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension;

/**
 * Creates a declarative {@link KotlinJvmApplication} DSL model, applies the official Kotlin and application plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
@BindsSoftwareType(StandaloneKotlinJvmLibraryPlugin.Binding.class)
public abstract class StandaloneKotlinJvmLibraryPlugin implements Plugin<Project> {

    public static final String KOTLIN_JVM_LIBRARY = "kotlinJvmLibrary";

    @Override
    public void apply(Project project) {

    }

    static class Binding implements SoftwareTypeBindingRegistration {
        @Override
        public void register(SoftwareTypeBindingBuilder builder) {
            builder.bindSoftwareType(KOTLIN_JVM_LIBRARY, KotlinJvmLibrary.class,
                    (context, definition, buildModel) -> {
                        Project project = context.getProject();
                        project.getPlugins().apply("org.jetbrains.kotlin.jvm");
                        ((DefaultKotlinJvmLibraryBuildModel)buildModel).setKotlinJvmExtension(
                                project.getExtensions().getByType(KotlinJvmProjectExtension.class)
                        );
                        ((DefaultKotlinJvmLibraryBuildModel)buildModel).setJavaPluginExtension(
                                project.getExtensions().getByType(JavaPluginExtension.class)
                        );

                        linkDslModelToPlugin(definition, buildModel, project.getConfigurations(), project.getTasks());
                    }).withBuildModelImplementationType(DefaultKotlinJvmLibraryBuildModel.class);
        }


        private void linkDslModelToPlugin(KotlinJvmLibrary dslModel, KotlinJvmLibraryBuildModel buildModel, ConfigurationContainer configurations, TaskContainer tasks) {
            KotlinPluginSupport.linkJavaVersion(dslModel, buildModel.getKotlinJvmExtension());
            JvmPluginSupport.linkMainSourceSourceSetDependencies(dslModel.getDependencies(), buildModel.getJavaPluginExtension(), configurations);

            configureTesting(dslModel, configurations, tasks);
        }

        private void configureTesting(KotlinJvmLibrary dslModel, ConfigurationContainer configurations, TaskContainer tasks) {
            configurations.getByName("testImplementation").fromDependencyCollector(dslModel.getTesting().getDependencies().getImplementation());
            configurations.getByName("testCompileOnly").fromDependencyCollector(dslModel.getTesting().getDependencies().getCompileOnly());
            configurations.getByName("testRuntimeOnly").fromDependencyCollector(dslModel.getTesting().getDependencies().getRuntimeOnly());

            tasks.withType(Test.class).configureEach(Test::useJUnitPlatform);
        }
    }
}
