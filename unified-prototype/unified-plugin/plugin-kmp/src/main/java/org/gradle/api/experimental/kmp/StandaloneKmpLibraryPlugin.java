package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.kmp.internal.KotlinPluginSupport;
import org.gradle.api.internal.plugins.BindsSoftwareType;
import org.gradle.api.internal.plugins.SoftwareTypeBindingBuilder;
import org.gradle.api.internal.plugins.SoftwareTypeBindingRegistration;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.TaskContainer;
import org.jetbrains.kotlin.gradle.dsl.JvmTarget;
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension;

/**
 * Creates a declarative {@link KmpLibrary} DSL model, applies the official KMP plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings({"UnstableApiUsage"})
@BindsSoftwareType(StandaloneKmpLibraryPlugin.Binding.class)
public abstract class StandaloneKmpLibraryPlugin implements Plugin<Project> {
    public static final String KOTLIN_LIBRARY = "kotlinLibrary";

    @Override
    public void apply(Project project) {
        // In order for function extraction from the DependencyCollector on the library deps to work, configurations must exist
        // Matching the names of the getters on LibraryDependencies
        project.getConfigurations().dependencyScope("api").get();
        project.getConfigurations().dependencyScope("implementation").get();
        project.getConfigurations().dependencyScope("compileOnly").get();
        project.getConfigurations().dependencyScope("runtimeOnly").get();
    }

    static class Binding implements SoftwareTypeBindingRegistration {
        @Override
        public void register(SoftwareTypeBindingBuilder builder) {
            builder.bindSoftwareType(KOTLIN_LIBRARY, KmpLibrary.class,
                    (context, definition, buildModel) -> {
                        Project project = context.getProject();

                        // Apply the official KMP plugin
                        project.getPlugins().apply("org.jetbrains.kotlin.multiplatform");
                        ((DefaultKotlinMultiplatformBuildModel)buildModel).setKotlinMultiplatformExtension(
                                project.getExtensions().getByType(KotlinMultiplatformExtension.class)
                        );
                        buildModel.getGroup().convention(definition.getGroup());
                        buildModel.getVersion().convention(definition.getVersion());

                        // This stuff can be wired up immediately
                        linkDslModelToPluginLazy(definition, buildModel, project.getConfigurations());
                        // This stuff must be wired up in an afterEvaluate block
                        project.afterEvaluate(p -> {
                            ifPresent(buildModel.getGroup(), p::setGroup);
                            ifPresent(buildModel.getVersion(), p::setVersion);
                            linkDslModelToPlugin(definition, buildModel, p.getTasks());
                        });
                    }
            ).withBuildModelImplementationType(DefaultKotlinMultiplatformBuildModel.class);
        }

        /**
         * Performs linking actions that must occur within an afterEvaluate block.
         */
        private static void linkDslModelToPlugin(KmpLibrary definition, KotlinMultiplatformBuildModel buildModel, TaskContainer tasks) {
            KotlinMultiplatformExtension kotlin = buildModel.getKotlinMultiplatformExtension();

            // Link common properties
            kotlin.getSourceSets().configureEach(sourceSet -> {
                sourceSet.languageSettings(languageSettings -> {
                    ifPresent(definition.getLanguageVersion(), languageSettings::setLanguageVersion);
                    ifPresent(definition.getLanguageVersion(), languageSettings::setApiVersion);
                });
            });

            // TODO - figure out how to get rid of this task
            tasks.configureEach(task -> {
                if (task.getName().equals("jvmRun")) {
                    task.setEnabled(false);
                }
            });
        }

        /**
         * Performs linking actions that do not need to occur within an afterEvaluate block.
         */
        private static void linkDslModelToPluginLazy(KmpLibrary dslModel, KotlinMultiplatformBuildModel buildModel, ConfigurationContainer configurations) {
            KotlinMultiplatformExtension kotlin = buildModel.getKotlinMultiplatformExtension();

            // Link common dependencies
            KotlinPluginSupport.linkSourceSetToDependencies(configurations, kotlin.getSourceSets().getByName("commonMain"), dslModel.getDependencies());

            // Link JVM targets
            dslModel.getTargetsContainer().withType(KmpLibraryJvmTarget.class).all(target -> {
                kotlin.jvm(target.getName(), kotlinTarget -> {
                    KotlinPluginSupport.linkSourceSetToDependencies(
                            configurations,
                            kotlinTarget.getCompilations().getByName("main").getDefaultSourceSet(),
                            target.getDependencies()
                    );
                    kotlinTarget.getCompilations().configureEach(compilation -> {
                        compilation.getCompilerOptions().getOptions().getJvmTarget().set(target.getJdkVersion().map(value -> JvmTarget.Companion.fromTarget(String.valueOf(value))));
                    });
                });
            });

            // Link JS targets
            dslModel.getTargetsContainer().withType(KmpLibraryNodeJsTarget.class).all(target -> {
                kotlin.js(target.getName(), kotlinTarget -> {
                    kotlinTarget.nodejs();
                    KotlinPluginSupport.linkSourceSetToDependencies(
                            configurations,
                            kotlinTarget.getCompilations().getByName("main").getDefaultSourceSet(),
                            target.getDependencies()
                    );
                });
            });

            // Link Native targets
            dslModel.getTargetsContainer().withType(KmpLibraryNativeTarget.class).all(target -> {
                kotlin.macosArm64(target.getName(), kotlinTarget -> {
                    KotlinPluginSupport.linkSourceSetToDependencies(
                            configurations,
                            kotlinTarget.getCompilations().getByName("main").getDefaultSourceSet(),
                            target.getDependencies()
                    );
                });
            });

        }

        private static <T> void ifPresent(Property<T> property, Action<T> action) {
            if (property.isPresent()) {
                action.execute(property.get());
            }
        }
    }
}
