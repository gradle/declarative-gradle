package org.gradle.api.experimental.kmp;

import kotlin.Unit;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.common.CliApplicationConventionsPlugin;
import org.gradle.api.experimental.kmp.internal.KotlinPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.AbstractExecTask;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.kotlin.gradle.dsl.JvmTarget;
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension;

/**
 * Creates a declarative {@link KmpApplication} DSL model, applies the official KMP plugin,
 * and links the declarative model to the official plugin.
 */
abstract public class StandaloneKmpApplicationPlugin implements Plugin<Project> {

    public static final String KOTLIN_APPLICATION = "kotlinApplication";

    @SoftwareType(name = KOTLIN_APPLICATION, modelPublicType = KmpApplication.class)
    abstract public KmpApplication getKmpApplication();

    @Override
    public void apply(Project project) {
        KmpApplication dslModel = createDslModel(project);
        project.getExtensions().add(KOTLIN_APPLICATION, dslModel);

        project.afterEvaluate(p -> linkDslModelToPlugin(p, dslModel));

        // Apply the official KMP plugin
        project.getPlugins().apply("org.jetbrains.kotlin.multiplatform");
        project.getPlugins().apply(CliApplicationConventionsPlugin.class);

        linkDslModelToPluginLazy(project, dslModel);
    }

    /**
     * Performs linking actions that must occur within an afterEvaluate block.
     */
    private void linkDslModelToPlugin(Project project, KmpApplication dslModel) {
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
    }

    private KmpApplication createDslModel(Project project) {
        KmpApplication dslModel = getKmpApplication();

        // In order for function extraction from the DependencyCollector on the library deps to work, configurations must exist
        // Matching the names of the getters on LibraryDependencies
        project.getConfigurations().dependencyScope("implementation").get();
        project.getConfigurations().dependencyScope("compileOnly").get();
        project.getConfigurations().dependencyScope("runtimeOnly").get();

        return dslModel;
    }

    /**
     * Performs linking actions that do not need to occur within an afterEvaluate block.
     */
    private void linkDslModelToPluginLazy(Project project, KmpApplication dslModel) {
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
