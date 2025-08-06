package org.gradle.api.experimental.java;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.jvm.DefaultJavaBuildModel;
import org.gradle.api.experimental.jvm.JavaBuildModel;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.internal.plugins.*;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.plugins.jvm.JvmTestSuite;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.jvm.toolchain.JavaToolchainService;
import org.gradle.testing.base.TestingExtension;

import javax.inject.Inject;

/**
 * Creates a declarative {@link JavaLibrary} DSL model, applies the official Java library plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
@BindsSoftwareType(StandaloneJavaLibraryPlugin.Binding.class)
public abstract class StandaloneJavaLibraryPlugin implements Plugin<Project> {
    public static final String JAVA_LIBRARY = "javaLibrary";

    @Override
    public void apply(Project project) {
        project.getExtensions().getByType(TestingExtension.class).getSuites().withType(JvmTestSuite.class).named("test").configure(JvmTestSuite::useJUnitJupiter);
    }

    public abstract static class Binding implements SoftwareTypeBindingRegistration {
        @Override
        public void register(SoftwareTypeBindingBuilder builder) {
            builder.bindSoftwareType(JAVA_LIBRARY, JavaLibrary.class,
                    (context, definition, buildModel) -> {
                        Project project = context.getProject();
                        project.getPlugins().apply(JavaLibraryPlugin.class);
                        ((DefaultJavaBuildModel) buildModel).setJavaPluginExtension(project.getExtensions().getByType(JavaPluginExtension.class));

                        context.getObjectFactory().newInstance(ModelToPluginLinker.class).link(
                                definition,
                                buildModel,
                                project.getPluginManager(),
                                project.getConfigurations(),
                                project.getTasks()
                        );
                    }
            ).withBuildModelImplementationType(DefaultJavaBuildModel.class);
        }

        static abstract class ModelToPluginLinker {
            @Inject
            public ModelToPluginLinker() {
            }

            @Inject
            protected abstract JavaToolchainService getJavaToolchainService();

            private void link(JavaLibrary definition, JavaBuildModel buildModel, PluginManager pluginManager, ConfigurationContainer configurations, TaskContainer tasks) {
                pluginManager.withPlugin("java", plugin -> {
                    JvmPluginSupport.linkJavaVersion(definition, buildModel.getJavaPluginExtension());
                    JvmPluginSupport.linkMainSourceSourceSetDependencies(definition.getDependencies(), buildModel.getJavaPluginExtension(), configurations);
                    JvmPluginSupport.linkTestJavaVersion(definition.getTesting(), getJavaToolchainService(), tasks);
                    JvmPluginSupport.linkTestSourceSourceSetDependencies(definition.getTesting().getDependencies(), buildModel.getJavaPluginExtension(), configurations);
                });
            }
        }
    }
}
