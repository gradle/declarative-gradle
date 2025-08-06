package org.gradle.api.experimental.kotlin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.common.CliApplicationConventionsPlugin;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.experimental.kmp.internal.KotlinPluginSupport;
import org.gradle.api.internal.plugins.BindsSoftwareType;
import org.gradle.api.internal.plugins.SoftwareTypeBindingBuilder;
import org.gradle.api.internal.plugins.SoftwareTypeBindingRegistration;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaApplication;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.testing.Test;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension;

/**
 * Creates a declarative {@link KotlinJvmApplication} DSL model, applies the official Kotlin and application plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
@BindsSoftwareType(StandaloneKotlinJvmApplicationPlugin.Binding.class)
public abstract class StandaloneKotlinJvmApplicationPlugin implements Plugin<Project> {

    public static final String KOTLIN_JVM_APPLICATION = "kotlinJvmApplication";

    @Override
    public void apply(Project project) {

    }

    static class Binding implements SoftwareTypeBindingRegistration {
        @Override
        public void register(SoftwareTypeBindingBuilder builder) {
            builder.bindSoftwareType(KOTLIN_JVM_APPLICATION, KotlinJvmApplication.class,
                    (context, definition, buildModel) -> {
                        Project project = context.getProject();
                        project.getPlugins().apply(ApplicationPlugin.class);
                        project.getPlugins().apply("org.jetbrains.kotlin.jvm");
                        project.getPlugins().apply(CliApplicationConventionsPlugin.class);
                        ((DefaultKotlinJvmApplicationBuildModel) buildModel).setKotlinJvmExtension(
                                project.getExtensions().getByType(KotlinJvmProjectExtension.class)
                        );
                        ((DefaultKotlinJvmLibraryBuildModel)buildModel).setJavaPluginExtension(
                                project.getExtensions().getByType(JavaPluginExtension.class)
                        );
                        ((DefaultKotlinJvmApplicationBuildModel)buildModel).setJavaApplicationExtension(
                                project.getExtensions().getByType(JavaApplication.class)
                        );

                        linkDslModelToPlugin(definition, buildModel, project.getConfigurations(), project.getTasks());
                    }).withBuildModelImplementationType(DefaultKotlinJvmApplicationBuildModel.class);
        }


        private void linkDslModelToPlugin(KotlinJvmApplication dslModel, KotlinJvmApplicationBuildModel buildModel, ConfigurationContainer configurations, TaskContainer tasks) {
            KotlinPluginSupport.linkJavaVersion(dslModel, buildModel.getKotlinJvmExtension());
            JvmPluginSupport.linkApplicationMainClass(dslModel, buildModel.getJavaApplicationExtension());
            JvmPluginSupport.linkMainSourceSourceSetDependencies(dslModel.getDependencies(), buildModel.getJavaPluginExtension(), configurations);
            configureTesting(dslModel, configurations, tasks);

            dslModel.getRunTasks().add(tasks.named("run"));
        }

        private void configureTesting(KotlinJvmApplication dslModel, ConfigurationContainer configurations, TaskContainer tasks) {
            configurations.getByName("testImplementation").fromDependencyCollector(dslModel.getTesting().getDependencies().getImplementation());
            configurations.getByName("testCompileOnly").fromDependencyCollector(dslModel.getTesting().getDependencies().getCompileOnly());
            configurations.getByName("testRuntimeOnly").fromDependencyCollector(dslModel.getTesting().getDependencies().getRuntimeOnly());

            tasks.withType(Test.class).configureEach(Test::useJUnitPlatform);
        }
    }
}
