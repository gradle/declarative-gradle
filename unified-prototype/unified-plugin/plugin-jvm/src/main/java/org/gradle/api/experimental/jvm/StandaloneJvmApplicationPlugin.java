package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.DependencyFactory;
import org.gradle.api.experimental.common.CliApplicationConventionsPlugin;

import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.internal.plugins.BindsSoftwareType;
import org.gradle.api.internal.plugins.SoftwareTypeBindingBuilder;
import org.gradle.api.internal.plugins.SoftwareTypeBindingRegistration;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaApplication;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.internal.JavaPluginHelper;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.toolchain.JavaToolchainService;

import javax.inject.Inject;

/**
 * Creates a declarative {@link JvmApplication} DSL model, applies the official Jvm plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
@BindsSoftwareType(StandaloneJvmApplicationPlugin.Binding.class)
public abstract class StandaloneJvmApplicationPlugin implements Plugin<Project> {

    public static final String JVM_APPLICATION = "jvmApplication";

    @Override
    public void apply(Project project) {

    }

    static class Binding implements SoftwareTypeBindingRegistration {
        @Override
        public void register(SoftwareTypeBindingBuilder builder) {
            builder.bindSoftwareType(JVM_APPLICATION, JvmApplication.class,
                    (context, definition, buildModel) -> {
                        Project project = context.getProject();
                        project.getPlugins().apply(ApplicationPlugin.class);
                        project.getPlugins().apply(CliApplicationConventionsPlugin.class);
                        ((DefaultJavaApplicationBuildModel) buildModel).setJavaPluginExtension(
                                project.getExtensions().getByType(JavaPluginExtension.class)
                        );
                        ((DefaultJavaApplicationBuildModel) buildModel).setJavaApplicationExtension(
                                project.getExtensions().getByType(JavaApplication.class)
                        );

                        context.getObjectFactory().newInstance(ModelToPluginLinker.class).link(
                                definition,
                                buildModel,
                                project.getConfigurations(),
                                project.getTasks(),
                                project.getLayout(),
                                project.getProviders(),
                                project.getDependencyFactory(),
                                JavaPluginHelper.getJavaComponent(project).getMainFeature().getSourceSet()
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

        private void link(
                JvmApplication dslModel,
                JavaApplicationBuildModel buildModel,
                ConfigurationContainer configurations,
                TaskContainer tasks,
                ProjectLayout projectLayout,
                ProviderFactory providers,
                DependencyFactory dependencyFactory,
                SourceSet commonSources) {

            JvmPluginSupport.setupCommonSourceSet(commonSources, projectLayout);
            JvmPluginSupport.linkSourceSetToDependencies(commonSources, dslModel.getDependencies(), configurations);

            JvmPluginSupport.linkJavaVersion(dslModel, buildModel.getJavaPluginExtension(), providers);
            JvmPluginSupport.linkApplicationMainClass(dslModel, buildModel.getJavaApplicationExtension());
            dslModel.getTargets().withType(JavaTarget.class).all(target -> {
                SourceSet sourceSet = JvmPluginSupport.createTargetSourceSet(target, commonSources, getJavaToolchainService(), buildModel.getJavaPluginExtension(), tasks, configurations, dependencyFactory);

                // Link dependencies to DSL
                JvmPluginSupport.linkSourceSetToDependencies(sourceSet, target.getDependencies(), configurations);

                // Create a run task
                TaskProvider<JavaExec> runTask = tasks.register(sourceSet.getTaskName("run", null), JavaExec.class, task -> {
                    task.getMainClass().set(dslModel.getMainClass());
                    task.getJvmArguments().set(dslModel.getJvmArguments());
                    task.setClasspath(sourceSet.getRuntimeClasspath());
                });
                dslModel.getRunTasks().add(runTask);
            });
        }
    }
}
