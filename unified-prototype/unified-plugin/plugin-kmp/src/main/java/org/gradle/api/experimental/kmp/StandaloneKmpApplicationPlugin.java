package org.gradle.api.experimental.kmp;

import kotlin.Unit;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.common.CliApplicationConventionsPlugin;
import org.gradle.api.experimental.kmp.internal.KotlinPluginSupport;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.internal.plugins.BindsSoftwareType;
import org.gradle.api.internal.plugins.SoftwareTypeBindingBuilder;
import org.gradle.api.internal.plugins.SoftwareTypeBindingRegistration;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.AbstractExecTask;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.testing.Test;
import org.jetbrains.kotlin.gradle.dsl.JvmTarget;
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension;
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation;
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest;
import org.apache.commons.text.WordUtils;

/**
 * Creates a declarative {@link KmpApplication} DSL model, applies the official KMP plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings({"UnstableApiUsage", "CodeBlock2Expr"})
@BindsSoftwareType(StandaloneKmpApplicationPlugin.Binding.class)
public abstract class StandaloneKmpApplicationPlugin implements Plugin<Project> {
    public static final String KOTLIN_APPLICATION = "kotlinApplication";

    @Override
    public void apply(Project project) {
        // In order for function extraction from the DependencyCollector on the library deps to work, configurations must exist
        // Matching the names of the getters on LibraryDependencies
        project.getConfigurations().dependencyScope("implementation").get();
        project.getConfigurations().dependencyScope("compileOnly").get();
        project.getConfigurations().dependencyScope("runtimeOnly").get();
    }

    static class Binding implements SoftwareTypeBindingRegistration {
        @Override
        public void register(SoftwareTypeBindingBuilder builder) {
            builder.bindSoftwareType(KOTLIN_APPLICATION, KmpApplication.class,
                    (context, definition, buildModel) -> {
                        Project project = context.getProject();

                        // Set conventional custom test suit locations as src/<SUITE_NAME>
                        definition.getTargetsContainer().withType(KmpApplicationJvmTarget.class).all(target -> {
                            target.getTesting().getTestSuites().forEach((name, testSuite) -> {
                                Directory srcRoot = project.getLayout().getProjectDirectory().dir("src/jvm" + WordUtils.capitalize(name));
                                testSuite.getSourceRoot().convention(srcRoot);
                            });
                        });

                        // Apply the official KMP plugin
                        project.getPlugins().apply("org.jetbrains.kotlin.multiplatform");
                        project.getPlugins().apply(CliApplicationConventionsPlugin.class);

                        ((DefaultKotlinMultiplatformBuildModel)buildModel).setKotlinMultiplatformExtension(
                                project.getExtensions().getByType(KotlinMultiplatformExtension.class)
                        );
                        buildModel.getGroup().convention(definition.getGroup());
                        buildModel.getVersion().convention(definition.getVersion());

                        // This stuff can be wired up immediately
                        linkDslModelToPluginLazy(definition, buildModel, project.getConfigurations(), project.getTasks());
                        // This stuff must be wired up in an afterEvaluate block
                        project.afterEvaluate(p -> {
                            ifPresent(buildModel.getGroup(), p::setGroup);
                            ifPresent(buildModel.getVersion(), p::setVersion);
                            linkDslModelToPlugin(definition, buildModel, project.getConfigurations(), project.getTasks(), project.getObjects());
                        });
                    }
            ).withBuildModelImplementationType(DefaultKotlinMultiplatformBuildModel.class);
        }

        /**
         * Performs linking actions that must occur within an afterEvaluate block.
         */
        private static void linkDslModelToPlugin(KmpApplication definition, KotlinMultiplatformBuildModel buildModel, ConfigurationContainer configurations, TaskContainer tasks, ObjectFactory objects) {
            KotlinMultiplatformExtension kotlin = buildModel.getKotlinMultiplatformExtension();

            // Link common properties
            kotlin.getSourceSets().configureEach(sourceSet -> {
                sourceSet.languageSettings(languageSettings -> {
                    ifPresent(definition.getLanguageVersion(), languageSettings::setLanguageVersion);
                    ifPresent(definition.getLanguageVersion(), languageSettings::setApiVersion);
                });
            });

            // Link Native targets
            definition.getTargetsContainer().withType(KmpApplicationNativeTarget.class).all(target -> {
                kotlin.macosArm64(target.getName(), kotlinTarget -> {
                    kotlinTarget.binaries(nativeBinaries -> {
                        nativeBinaries.executable(executable -> {
                            executable.entryPoint(target.getEntryPoint().get());
                            TaskProvider<AbstractExecTask<?>> runTask = executable.getRunTaskProvider();
                            if (runTask != null) {
                                definition.getRunTasks().add(runTask);
                            }
                        });
                    });
                });
            });

            // Add common JVM testing dependencies if the JVM target is a part of this build
            if (null != kotlin.getSourceSets().findByName("jvmTest")) {
                KotlinPluginSupport.linkSourceSetToDependencies(configurations, kotlin.getSourceSets().getByName("jvmTest"), definition.getTargetsContainer().getByName("jvm").getTesting().getDependencies());
                tasks.withType(KotlinJvmTest.class).forEach(Test::useJUnitPlatform);
            }

            // Create all custom JVM test suites
            definition.getTargetsContainer().withType(KmpApplicationJvmTarget.class).all(target -> {
                kotlin.jvm(target.getName(), kotlinTarget -> {
                    target.getTesting().getTestSuites().forEach((name, testSuite) -> {
                        // Create a new compilation for the test suite
                        String suiteCompilationName = "suite" + WordUtils.capitalize(name) + "Compilation";
                        // Note: "register" won't work here, lazy APIs not working right on this container, just create
                        KotlinJvmCompilation suiteCompilation = kotlinTarget.getCompilations().create(suiteCompilationName, compilation -> {
                            compilation.associateWith(kotlin.jvm().compilations.getByName("test"));
                            compilation.getDefaultSourceSet().getKotlin().srcDir(testSuite.getSourceRoot());

                            // Add testing dependencies specific to each JVM test suite
                            KotlinPluginSupport.linkSourceSetToDependencies(
                                    configurations,
                                    compilation.getDefaultSourceSet(),
                                    testSuite.getDependencies()
                            );
                        });

                        // Create test task for the new compilation
                        String suiteTestTaskName = "suite" +  WordUtils.capitalize(name) + "Test";
                        Provider<KotlinJvmTest> suiteTestTask = tasks.register(suiteTestTaskName, KotlinJvmTest.class, task -> {
                            task.setTargetName(suiteCompilation.getTarget().getName());
                            task.useJUnitPlatform();
                            task.dependsOn(suiteCompilation.getCompileTaskProvider());
                            task.setTestClassesDirs(suiteCompilation.getCompileTaskProvider().get().getOutputs().getFiles());

                            ConfigurableFileCollection testRuntimeClasspath = objects.fileCollection();
                            testRuntimeClasspath.from(suiteCompilation.getCompileTaskProvider().get().getOutputs().getFiles());
                            testRuntimeClasspath.from(configurations.named(suiteCompilation.getRuntimeDependencyConfigurationName()).get());
                            task.setClasspath(testRuntimeClasspath);
                        });

                        // New tests are included in jvm tests
                        tasks.named("jvmTest").configure(task -> {
                            task.dependsOn(suiteTestTask);
                        });
                    });
                });
            });
        }

        /**
         * Performs linking actions that do not need to occur within an afterEvaluate block.
         */
        @SuppressWarnings("deprecation")
        private static void linkDslModelToPluginLazy(KmpApplication definition, KotlinMultiplatformBuildModel buildModel, ConfigurationContainer configurations, TaskContainer tasks) {
            KotlinMultiplatformExtension kotlin = buildModel.getKotlinMultiplatformExtension();

            // Link common dependencies
            KotlinPluginSupport.linkSourceSetToDependencies(configurations, kotlin.getSourceSets().getByName("commonMain"), definition.getDependencies());

            // Link JVM targets
            definition.getTargetsContainer().withType(KmpApplicationJvmTarget.class).all(target -> {
                kotlin.jvm(target.getName(), kotlinTarget -> {
                    KotlinPluginSupport.linkSourceSetToDependencies(
                            configurations,
                            kotlinTarget.getCompilations().getByName("main").getDefaultSourceSet(),
                            target.getDependencies()
                    );
                    kotlinTarget.getCompilations().configureEach(compilation -> {
                        compilation.getCompilerOptions().getOptions().getJvmTarget().set(target.getJdkVersion().map(value -> JvmTarget.Companion.fromTarget(String.valueOf(value))));
                    });
                    kotlinTarget.mainRun(kotlinJvmRunDsl -> {
                        kotlinJvmRunDsl.getMainClass().set(target.getMainClass());
                        // The task is not registered until this block of code runs, but the block is deferred until some arbitrary point in time
                        // So, wire up the task when this block runs
                        definition.getRunTasks().add(tasks.named(target.getName() + "Run"));
                        return Unit.INSTANCE;
                    });
                });
            });

            // Link JS targets
            definition.getTargetsContainer().withType(KmpApplicationNodeJsTarget.class).all(target -> {
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
            definition.getTargetsContainer().withType(KmpApplicationNativeTarget.class).all(target -> {
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
