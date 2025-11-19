package org.gradle.api.experimental.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.plugin.internal.DefaultJaveGradlePluginBuildModel;
import org.gradle.api.internal.plugins.BindsProjectType;
import org.gradle.api.internal.plugins.ProjectTypeBinding;
import org.gradle.api.internal.plugins.ProjectTypeBindingBuilder;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;

/**
 * Creates a declarative {@link JavaGradlePlugin} DSL model, applies the Gradle java-gradle-plugin plugin,
 * and links the declarative definition to the Gradle plugin.
 */
@SuppressWarnings("UnstableApiUsage")
@BindsProjectType(JavaGradlePluginPlugin.Binding.class)
public abstract class JavaGradlePluginPlugin implements Plugin<Project> {
    public static final String JAVA_GRADLE_PLUGIN = "javaGradlePlugin";

    static class Binding implements ProjectTypeBinding {
        @Override
        public void bind(ProjectTypeBindingBuilder builder) {
            builder.bindProjectType(JAVA_GRADLE_PLUGIN, JavaGradlePlugin.class, (context, definition, buildModel) -> {
                context.getProject().getPluginManager().apply("java-gradle-plugin");

                ((DefaultJaveGradlePluginBuildModel)buildModel).setDevelopmentExtension(
                    context.getProject().getExtensions().getByType(GradlePluginDevelopmentExtension.class));

                context.getProject().afterEvaluate(project -> {
                    project.setDescription(definition.getDescription().get());
                    project.getConfigurations().getByName("api").fromDependencyCollector(definition.getDependencies().getApi());
                    project.getConfigurations().getByName("implementation").fromDependencyCollector(definition.getDependencies().getImplementation());

                    GradlePluginDevelopmentExtension pluginDevelopmentExtension = project.getExtensions().getByType(GradlePluginDevelopmentExtension.class);
                    definition.getRegisters().forEach(registration -> {
                        pluginDevelopmentExtension.getPlugins().create(registration.getName(), p -> {
                            p.setId(registration.getName());
                            p.setImplementationClass(registration.getImplementationClass().get());
                            p.setDescription(registration.getDescription().get());
                        });
                    });
                });
            })
            .withBuildModelImplementationType(DefaultJaveGradlePluginBuildModel.class);
        }
    }

    @Override
    public void apply(Project project) { }
}
