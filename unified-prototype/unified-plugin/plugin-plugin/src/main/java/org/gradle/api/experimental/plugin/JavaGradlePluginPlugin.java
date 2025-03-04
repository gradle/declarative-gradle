package org.gradle.api.experimental.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;

/**
 * Creates a declarative {@link JavaGradlePlugin} DSL model, applies the Gradle java-gradle-plugin plugin,
 * and links the declarative definition to the Gradle plugin.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class JavaGradlePluginPlugin implements Plugin<Project> {
    public static final String JAVA_GRADLE_PLUGIN = "javaGradlePlugin";

    @SoftwareType(name = JAVA_GRADLE_PLUGIN, modelPublicType = JavaGradlePlugin.class)
    public abstract JavaGradlePlugin getGradlePlugin();

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("java-gradle-plugin");

        project.afterEvaluate(evaluatedProject -> {
            JavaGradlePlugin projectDefinition = getGradlePlugin();

            project.setDescription(projectDefinition.getDescription().get());
            project.getConfigurations().getByName("api").fromDependencyCollector(projectDefinition.getDependencies().getApi());
            project.getConfigurations().getByName("implementation").fromDependencyCollector(projectDefinition.getDependencies().getImplementation());

            GradlePluginDevelopmentExtension pluginDevelopmentExtension = project.getExtensions().getByType(GradlePluginDevelopmentExtension.class);
            projectDefinition.getRegisters().forEach(registration -> {
                pluginDevelopmentExtension.getPlugins().create(registration.getName(), p -> {
                    p.setId(registration.getName());
                    p.setImplementationClass(registration.getImplementationClass().get());
                    p.setDescription(registration.getDescription().get());
                });
            });
        });
    }
}
