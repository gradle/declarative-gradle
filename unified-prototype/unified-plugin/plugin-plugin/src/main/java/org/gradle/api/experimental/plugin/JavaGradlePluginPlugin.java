package org.gradle.api.experimental.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.plugin.internal.DefaultJaveGradlePluginBuildModel;
import org.gradle.api.plugins.PluginManager;
import org.gradle.features.annotations.BindsProjectType;
import org.gradle.features.binding.ProjectFeatureApplicationContext;
import org.gradle.features.binding.ProjectTypeApplyAction;
import org.gradle.features.binding.ProjectTypeBinding;
import org.gradle.features.binding.ProjectTypeBindingBuilder;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;

import javax.inject.Inject;

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
            builder.bindProjectType(JAVA_GRADLE_PLUGIN, JavaGradlePlugin.class, ApplyAction.class)
                .withUnsafeDefinition()
                .withUnsafeApplyAction()
                .withBuildModelImplementationType(DefaultJaveGradlePluginBuildModel.class);
        }

        @SuppressWarnings("UnstableApiUsage")
        static abstract class ApplyAction implements ProjectTypeApplyAction<JavaGradlePlugin, JavaGradlePluginBuildModel> {
            @Inject
            public ApplyAction() {
            }

            @Inject
            protected abstract PluginManager getPluginManager();

            @Inject
            protected abstract Project getProject();

            @Override
            public void apply(ProjectFeatureApplicationContext context, JavaGradlePlugin definition, JavaGradlePluginBuildModel buildModel) {
                getPluginManager().apply("java-gradle-plugin");

                ((DefaultJaveGradlePluginBuildModel) buildModel).setDevelopmentExtension(
                    getProject().getExtensions().getByType(GradlePluginDevelopmentExtension.class));

                getProject().afterEvaluate(project -> {
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
            }
        }
    }

    @Override
    public void apply(Project project) { }
}
