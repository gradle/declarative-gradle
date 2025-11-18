package org.gradle.api.experimental.java;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.common.CliApplicationConventionsPlugin;
import org.gradle.api.experimental.jvm.DefaultJavaApplicationBuildModel;
import org.gradle.api.experimental.jvm.DefaultJavaBuildModel;
import org.gradle.api.experimental.jvm.JavaApplicationBuildModel;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.internal.plugins.BindsProjectType;
import org.gradle.api.internal.plugins.ProjectTypeBindingBuilder;
import org.gradle.api.internal.plugins.ProjectTypeBinding;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.JvmTestSuitePlugin;
import org.gradle.api.plugins.jvm.JvmTestSuite;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.jvm.toolchain.JavaToolchainService;
import org.gradle.testing.base.TestingExtension;

import javax.inject.Inject;

import static org.gradle.api.plugins.JvmTestSuitePlugin.DEFAULT_TEST_SUITE_NAME;

/**
 * Creates a declarative {@link JavaApplication} DSL model, applies the official Java application plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
@BindsProjectType(StandaloneJavaApplicationPlugin.Binding.class)
public abstract class StandaloneJavaApplicationPlugin implements Plugin<Project> {
    public static final String JAVA_APPLICATION = "javaApplication";

    @Override
    public void apply(Project project) {
        project.getPluginManager().withPlugin("org.gradle.java",
                appliedPlugin -> project.getExtensions().getByType(TestingExtension.class).getSuites().withType(JvmTestSuite.class).named(DEFAULT_TEST_SUITE_NAME).configure(JvmTestSuite::useJUnitJupiter));
    }

    static class Binding implements ProjectTypeBinding {
        @Override
        public void bind(ProjectTypeBindingBuilder builder) {
            builder.bindProjectType(JAVA_APPLICATION, JavaApplication.class,
                    (context, definition, buildModel) -> {
                        Project project = context.getProject();
                        project.getPlugins().apply(ApplicationPlugin.class);
                        project.getPlugins().apply(CliApplicationConventionsPlugin.class);
                        ((DefaultJavaBuildModel) buildModel).setJavaPluginExtension(
                                project.getExtensions().getByType(JavaPluginExtension.class)
                        );
                        ((DefaultJavaApplicationBuildModel) buildModel).setJavaApplicationExtension(
                                project.getExtensions().getByType(org.gradle.api.plugins.JavaApplication.class)
                        );

                        context.getObjectFactory().newInstance(ModelToPluginLinker.class).link(
                                definition,
                                buildModel,
                                project.getConfigurations(),
                                project.getTasks()
                        );
                    }
            ).withBuildModelImplementationType(DefaultJavaApplicationBuildModel.class);
        }
    }

    static abstract class ModelToPluginLinker {
        @Inject
        public ModelToPluginLinker() {
        }

        @Inject
        protected abstract JavaToolchainService getJavaToolchainService();

        private void link(JavaApplication dslModel, JavaApplicationBuildModel buildModel, ConfigurationContainer configurations, TaskContainer tasks) {
            JvmPluginSupport.linkJavaVersion(dslModel, buildModel.getJavaPluginExtension());
            JvmPluginSupport.linkApplicationMainClass(dslModel, buildModel.getJavaApplicationExtension());
            JvmPluginSupport.linkMainSourceSourceSetDependencies(dslModel.getDependencies(), buildModel.getJavaPluginExtension(), configurations);
            JvmPluginSupport.linkTestJavaVersion(dslModel.getTesting(), getJavaToolchainService(), tasks);
            JvmPluginSupport.linkTestSourceSourceSetDependencies(dslModel.getTesting().getDependencies(), buildModel.getJavaPluginExtension(), configurations);

            dslModel.getRunTasks().add(tasks.named("run"));
        }
    }
}
