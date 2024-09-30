package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.kmp.internal.KotlinPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.provider.Property;
import org.jetbrains.kotlin.gradle.dsl.JvmTarget;
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension;

/**
 * Creates a declarative {@link KmpLibrary} DSL model, applies the official KMP plugin,
 * and links the declarative model to the official plugin.
 */
abstract public class StandaloneKmpLibraryPlugin implements Plugin<Project> {

    public static final String KOTLIN_LIBRARY = "kotlinLibrary";

    @SoftwareType(name = KOTLIN_LIBRARY, modelPublicType = KmpLibrary.class)
    abstract public KmpLibrary getKmpLibrary();

    @Override
    public void apply(Project project) {
        KmpLibrary dslModel = createDslModel(project);
        project.getExtensions().add(KOTLIN_LIBRARY, dslModel);

        project.afterEvaluate(p -> linkDslModelToPlugin(p, dslModel));

        // Apply the official KMP plugin
        project.getPlugins().apply("org.jetbrains.kotlin.multiplatform");

        linkDslModelToPluginLazy(project, dslModel);
    }

    private KmpLibrary createDslModel(Project project) {
        KmpLibrary dslModel = getKmpLibrary();

        // In order for function extraction from the DependencyCollector on the library deps to work, configurations must exist
        // Matching the names of the getters on LibraryDependencies
        project.getConfigurations().dependencyScope("api").get();
        project.getConfigurations().dependencyScope("implementation").get();
        project.getConfigurations().dependencyScope("compileOnly").get();
        project.getConfigurations().dependencyScope("runtimeOnly").get();

        return dslModel;
    }

    /**
     * Performs linking actions that must occur within an afterEvaluate block.
     */
    private void linkDslModelToPlugin(Project project, KmpLibrary dslModel) {
        KotlinMultiplatformExtension kotlin = project.getExtensions().getByType(KotlinMultiplatformExtension.class);

        // Link common properties
        kotlin.getSourceSets().configureEach(sourceSet -> {
            sourceSet.languageSettings(languageSettings -> {
                ifPresent(dslModel.getLanguageVersion(), languageSettings::setLanguageVersion);
                ifPresent(dslModel.getLanguageVersion(), languageSettings::setApiVersion);
            });
        });

        // TODO - figure out how to get rid of this task
        project.getTasks().configureEach(task -> {
            if (task.getName().equals("jvmRun")) {
                task.setEnabled(false);
            }
        });
    }

    /**
     * Performs linking actions that do not need to occur within an afterEvaluate block.
     */
    private void linkDslModelToPluginLazy(Project project, KmpLibrary dslModel) {
        KotlinMultiplatformExtension kotlin = project.getExtensions().getByType(KotlinMultiplatformExtension.class);

        // Link common dependencies
        KotlinPluginSupport.linkSourceSetToDependencies(project, kotlin.getSourceSets().getByName("commonMain"), dslModel.getDependencies());

        // Link JVM targets
        dslModel.getTargetsContainer().withType(KmpLibraryJvmTarget.class).all(target -> {
            kotlin.jvm(target.getName(), kotlinTarget -> {
                KotlinPluginSupport.linkSourceSetToDependencies(
                        project,
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
                        project,
                        kotlinTarget.getCompilations().getByName("main").getDefaultSourceSet(),
                        target.getDependencies()
                );
            });
        });

        // Link Native targets
        dslModel.getTargetsContainer().withType(KmpLibraryNativeTarget.class).all(target -> {
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
