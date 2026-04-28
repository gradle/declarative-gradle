package org.gradle.api.experimental.kotlin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.common.CliExecutablesSupport;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.experimental.kmp.internal.KotlinPluginSupport;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaApplication;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.testing.Test;
import org.gradle.features.annotations.BindsProjectType;
import org.gradle.features.binding.ProjectFeatureApplicationContext;
import org.gradle.features.binding.ProjectTypeApplyAction;
import org.gradle.features.binding.ProjectTypeBinding;
import org.gradle.features.binding.ProjectTypeBindingBuilder;
import org.gradle.features.registration.TaskRegistrar;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension;

import javax.inject.Inject;

/**
 * Creates a declarative {@link KotlinJvmApplication} DSL model, applies the official Kotlin and application plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
@BindsProjectType(StandaloneKotlinJvmApplicationPlugin.Binding.class)
public abstract class StandaloneKotlinJvmApplicationPlugin implements Plugin<Project> {

    public static final String KOTLIN_JVM_APPLICATION = "kotlinJvmApplication";

    @Override
    public void apply(Project project) {

    }

    static class Binding implements ProjectTypeBinding {
        @Override
        public void bind(ProjectTypeBindingBuilder builder) {
            builder.bindProjectType(KOTLIN_JVM_APPLICATION, KotlinJvmApplication.class, ApplyAction.class)
                .withUnsafeDefinition()
                .withUnsafeApplyAction()
                .withBuildModelImplementationType(DefaultKotlinJvmApplicationBuildModel.class);
        }

        @SuppressWarnings("UnstableApiUsage")
        static abstract class ApplyAction implements ProjectTypeApplyAction<KotlinJvmApplication, KotlinJvmApplicationBuildModel> {
            @Inject
            public ApplyAction() {
            }

            @Inject
            protected abstract PluginManager getPluginManager();

            @Inject
            protected abstract TaskRegistrar getTaskRegistrar();

            @Inject
            protected abstract Project getProject();

            @Override
            public void apply(ProjectFeatureApplicationContext context, KotlinJvmApplication definition, KotlinJvmApplicationBuildModel buildModel) {
                getPluginManager().apply(ApplicationPlugin.class);
                getPluginManager().apply("org.jetbrains.kotlin.jvm");
                CliExecutablesSupport.configureRunTasks(getTaskRegistrar(), buildModel);
                ((DefaultKotlinJvmApplicationBuildModel) buildModel).setKotlinJvmExtension(
                        getProject().getExtensions().getByType(KotlinJvmProjectExtension.class)
                );
                ((DefaultKotlinJvmLibraryBuildModel) buildModel).setJavaPluginExtension(
                        getProject().getExtensions().getByType(JavaPluginExtension.class)
                );
                ((DefaultKotlinJvmApplicationBuildModel) buildModel).setJavaApplicationExtension(
                        getProject().getExtensions().getByType(JavaApplication.class)
                );

                linkDslModelToPlugin(definition, buildModel, getProject().getConfigurations(), getProject().getTasks());
            }

            private void linkDslModelToPlugin(KotlinJvmApplication dslModel, KotlinJvmApplicationBuildModel buildModel, ConfigurationContainer configurations, TaskContainer tasks) {
                KotlinPluginSupport.linkJavaVersion(dslModel, buildModel.getKotlinJvmExtension());
                JvmPluginSupport.linkApplicationMainClass(dslModel, buildModel.getJavaApplicationExtension());
                JvmPluginSupport.linkMainSourceSourceSetDependencies(dslModel.getDependencies(), buildModel.getJavaPluginExtension(), configurations);
                configureTesting(dslModel, configurations, tasks);

                buildModel.getRunTasks().add(tasks.named("run"));
            }

            private void configureTesting(KotlinJvmApplication dslModel, ConfigurationContainer configurations, TaskContainer tasks) {
                configurations.getByName("testImplementation").fromDependencyCollector(dslModel.getTesting().getDependencies().getImplementation());
                configurations.getByName("testCompileOnly").fromDependencyCollector(dslModel.getTesting().getDependencies().getCompileOnly());
                configurations.getByName("testRuntimeOnly").fromDependencyCollector(dslModel.getTesting().getDependencies().getRuntimeOnly());

                tasks.withType(Test.class).configureEach(Test::useJUnitPlatform);
            }
        }
    }
}
