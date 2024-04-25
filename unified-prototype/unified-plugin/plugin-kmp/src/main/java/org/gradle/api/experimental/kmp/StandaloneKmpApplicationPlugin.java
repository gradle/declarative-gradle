package org.gradle.api.experimental.kmp;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.plugins.software.SoftwareType;
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

        // Apply the official KMP plugin
        project.getPlugins().apply("org.jetbrains.kotlin.multiplatform");

        linkDslModelToPluginLazy(project, dslModel);
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

        // Link JVM targets
        dslModel.getTargets().withType(KmpJvmTarget.class).all(target -> {
            kotlin.jvm(target.getName(), kotlinTarget -> {
            });
        });

        // Link JS targets
        dslModel.getTargets().withType(KmpJsTarget.class).all(target -> {
            kotlin.js(target.getName(), kotlinTarget -> {
            });
        });

    }
}
