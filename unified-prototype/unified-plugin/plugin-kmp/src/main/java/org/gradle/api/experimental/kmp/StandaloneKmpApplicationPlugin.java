package org.gradle.api.experimental.kmp;

import kotlin.Unit;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.common.CliApplicationConventionsPlugin;
import org.gradle.api.experimental.kmp.internal.KotlinPluginSupport;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.AbstractExecTask;
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
public abstract class StandaloneKmpApplicationPlugin implements Plugin<Project> {
    public static final String KOTLIN_APPLICATION = "kotlinApplication";

    @SoftwareType(name = KOTLIN_APPLICATION, modelPublicType = KmpApplication.class)
    public abstract KmpApplication getKmpApplication();

    @Override
    public void apply(Project project) {
        PluginWiring.wirePlugin(project, getKmpApplication());
    }

    public static final class PluginWiring {
        private PluginWiring() { /* not instantiable */ }

        public static void wirePlugin(Project project, KmpApplication initialDslModel) {
            KmpApplication dslModel = setupDslModel(project, initialDslModel);

            project.afterEvaluate(p -> linkDslModelToPlugin(p, dslModel));

            // Apply the official KMP plugin
            project.getPlugins().apply("org.jetbrains.kotlin.multiplatform");
            project.getPlugins().apply(CliApplicationConventionsPlugin.class);

            linkDslModelToPluginLazy(project, dslModel);
        }

        private static KmpApplication setupDslModel(Project project, KmpApplication dslModel) {
            // In order for function extraction from the DependencyCollector on the library deps to work, configurations must exist
            // Matching the names of the getters on LibraryDependencies
            project.getConfigurations().dependencyScope("implementation").get();
            project.getConfigurations().dependencyScope("compileOnly").get();
            project.getConfigurations().dependencyScope("runtimeOnly").get();

            // Set conventional custom test suit locations as src/<SUITE_NAME>
            dslModel.getTargetsContainer().withType(KmpApplicationJvmTarget.class).all(target -> {
                target.getTesting().getTestSuites().forEach((name, testSuite) -> {
                    Directory srcRoot = project.getLayout().getProjectDirectory().dir("src/jvm" + WordUtils.capitalize(name));
                    testSuite.getSourceRoot().convention(srcRoot);
                });
            });

            return dslModel;
        }

        /**
         * Performs linking actions that must occur within an afterEvaluate block.
         */
        private static void linkDslModelToPlugin(Project project, KmpApplication dslModel) {
            KotlinMultiplatformExtension kotlin = project.getExtensions().getByType(KotlinMultiplatformExtension.class);

            // Link common properties
            kotlin.getSourceSets().configureEach(sourceSet -> {
                sourceSet.languageSettings(languageSettings -> {
                    ifPresent(dslModel.getLanguageVersion(), languageSettings::setLanguageVersion);
                    ifPresent(dslModel.getLanguageVersion(), languageSettings::setApiVersion);
                });
            });

            // Link Native targets
            dslModel.getTargetsContainer().withType(KmpApplicationNativeTarget.class).all(target -> {
                kotlin.macosArm64(target.getName(), kotlinTarget -> {
                    kotlinTarget.binaries(nativeBinaries -> {
                        nativeBinaries.executable(executable -> {
                            executable.entryPoint(target.getEntryPoint().get());
                            TaskProvider<AbstractExecTask<?>> runTask = executable.getRunTaskProvider();
                            if (runTask != null) {
                                dslModel.getRunTasks().add(runTask);
                            }
                        });
                    });
                });
            });

            // Add common JVM testing dependencies if the JVM target is a part of this build
            if (null != kotlin.getSourceSets().findByName("jvmTest")) {
                KotlinPluginSupport.linkSourceSetToDependencies(project, kotlin.getSourceSets().getByName("jvmTest"), dslModel.getTargetsContainer().getByName("jvm").getTesting().getDependencies());
                project.getTasks().withType(KotlinJvmTest.class).forEach(Test::useJUnitPlatform);
            }

            // Create all custom JVM test suites
            dslModel.getTargetsContainer().withType(KmpApplicationJvmTarget.class).all(target -> {
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
                                    project,
                                    compilation.getDefaultSourceSet(),
                                    testSuite.getDependencies()
                            );
                        });

                        // Create test task for the new compilation
                        String suiteTestTaskName = "suite" +  WordUtils.capitalize(name) + "Test";
                        Provider<KotlinJvmTest> suiteTestTask = project.getTasks().register(suiteTestTaskName, KotlinJvmTest.class, task -> {
                            task.setTargetName(suiteCompilation.getTarget().getName());
                            task.useJUnitPlatform();
                            task.dependsOn(suiteCompilation.getCompileTaskProvider());
                            task.setTestClassesDirs(suiteCompilation.getCompileTaskProvider().get().getOutputs().getFiles());

                            ConfigurableFileCollection testRuntimeClasspath = project.getObjects().fileCollection();
                            testRuntimeClasspath.from(suiteCompilation.getCompileTaskProvider().get().getOutputs().getFiles());
                            testRuntimeClasspath.from(project.getConfigurations().named(suiteCompilation.getRuntimeDependencyConfigurationName()).get());
                            task.setClasspath(testRuntimeClasspath);
                        });

                        // New tests are included in jvm tests
                        project.getTasks().named("jvmTest").configure(task -> {
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
        private static void linkDslModelToPluginLazy(Project project, KmpApplication dslModel) {
            KotlinMultiplatformExtension kotlin = project.getExtensions().getByType(KotlinMultiplatformExtension.class);

            // Link common dependencies
            KotlinPluginSupport.linkSourceSetToDependencies(project, kotlin.getSourceSets().getByName("commonMain"), dslModel.getDependencies());

            // Link JVM targets
            dslModel.getTargetsContainer().withType(KmpApplicationJvmTarget.class).all(target -> {
                kotlin.jvm(target.getName(), kotlinTarget -> {
                    KotlinPluginSupport.linkSourceSetToDependencies(
                            project,
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
                        dslModel.getRunTasks().add(project.getTasks().named(target.getName() + "Run"));
                        return Unit.INSTANCE;
                    });
                });
            });

            // Link JS targets
            dslModel.getTargetsContainer().withType(KmpApplicationNodeJsTarget.class).all(target -> {
                kotlin.js(target.getName(), kotlinTarget -> {
                    kotlinTarget.nodejs();
                    KotlinPluginSupport.linkSourceSetToDependencies(
                            project,
                            kotlinTarget.getCompilations().getByName("main").getDefaultSourceSet(),
                            target.getDependencies()
                    );
                });
            });

            // Link Native targets
            dslModel.getTargetsContainer().withType(KmpApplicationNativeTarget.class).all(target -> {
                kotlin.macosArm64(target.getName(), kotlinTarget -> {
                    KotlinPluginSupport.linkSourceSetToDependencies(
                            project,
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
