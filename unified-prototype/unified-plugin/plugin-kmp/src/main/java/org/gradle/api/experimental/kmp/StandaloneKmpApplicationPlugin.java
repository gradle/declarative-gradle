package org.gradle.api.experimental.kmp;

import kotlin.Unit;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.kmp.internal.KotlinPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.provider.Property;
import org.jetbrains.kotlin.gradle.dsl.JvmTarget;
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension;

/**
 * Creates a declarative {@link KmpApplication} DSL model, applies the official KMP plugin,
 * and links the declarative model to the official plugin.
 */
abstract public class StandaloneKmpApplicationPlugin implements Plugin<Project> {
    @SoftwareType(name = "kotlinApplication", modelPublicType = KmpApplication.class)
    abstract public KmpApplication getKmpApplication();

    @Override
    public void apply(Project project) {
        KmpApplication dslModel = createDslModel(project);

        project.afterEvaluate(p -> linkDslModelToPlugin(p, dslModel));

        // Apply the official KMP plugin
        project.getPlugins().apply("org.jetbrains.kotlin.multiplatform");

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
        dslModel.getTargets().withType(KmpApplicationNativeTarget.class).all(target -> {
            kotlin.macosArm64(target.getName(), kotlinTarget -> {
                kotlinTarget.binaries(nativeBinaries -> {
                    nativeBinaries.executable(executable -> {
                        executable.entryPoint(target.getEntryPoint().get());
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
        dslModel.getTargets().withType(KmpApplicationJvmTarget.class).all(target -> {
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
                    return Unit.INSTANCE;
                });
            });
        });

        // Link JS targets
        dslModel.getTargets().withType(KmpApplicationNodeJsTarget.class).all(target -> {
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
        dslModel.getTargets().withType(KmpApplicationNativeTarget.class).all(target -> {
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
