package org.gradle.api.experimental.kmp;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.plugins.software.SoftwareType;

/**
 * Creates a declarative {@link KmpApplication} DSL model, applies the official KMP plugin,
 * and links the declarative model to the official plugin.
 */
abstract public class StandaloneKmpApplicationPlugin implements Plugin<Project> {
    @SoftwareType(name = "kotlinApplication", modelPublicType = KmpApplication.class)
    abstract public KmpApplication getKmpApplication();

    @Override
    public void apply(Project project) {
        createDslModel(project);
    }

    private KmpApplication createDslModel(Project project) {
        KmpApplication dslModel = getKmpApplication();

        // In order for function extraction from the DependencyCollector on the library deps to work, configurations must exist
        // Matching the names of the getters on LibraryDependencies
        project.getConfigurations().dependencyScope("api").get();
        project.getConfigurations().dependencyScope("implementation").get();
        project.getConfigurations().dependencyScope("compileOnly").get();
        project.getConfigurations().dependencyScope("runtimeOnly").get();

        return dslModel;
    }
}
