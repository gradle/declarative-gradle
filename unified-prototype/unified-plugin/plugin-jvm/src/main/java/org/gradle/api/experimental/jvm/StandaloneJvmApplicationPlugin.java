package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.DependencyFactory;
import org.gradle.api.experimental.common.CliExecutablesSupport;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaApplication;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.plugins.internal.JavaPluginHelper;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.features.annotations.BindsProjectType;
import org.gradle.features.binding.ProjectFeatureApplicationContext;
import org.gradle.features.binding.ProjectTypeApplyAction;
import org.gradle.features.binding.ProjectTypeBinding;
import org.gradle.features.binding.ProjectTypeBindingBuilder;
import org.gradle.features.file.ProjectFeatureLayout;
import org.gradle.features.registration.TaskRegistrar;
import org.gradle.jvm.toolchain.JavaToolchainService;

import javax.inject.Inject;

/**
 * Creates a declarative {@link JvmApplication} DSL model, applies the official Jvm plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
@BindsProjectType(StandaloneJvmApplicationPlugin.Binding.class)
public abstract class StandaloneJvmApplicationPlugin implements Plugin<Project> {

    public static final String JVM_APPLICATION = "jvmApplication";

    @Override
    public void apply(Project project) {

    }

    static class Binding implements ProjectTypeBinding {
        @Override
        public void bind(ProjectTypeBindingBuilder builder) {
            builder.bindProjectType(JVM_APPLICATION, JvmApplication.class, ApplyAction.class)
                    .withUnsafeDefinition()
                    .withUnsafeApplyAction()
                    .withBuildModelImplementationType(DefaultJavaApplicationBuildModel.class);
        }

        @SuppressWarnings("UnstableApiUsage")
        static abstract class ApplyAction implements ProjectTypeApplyAction<JvmApplication, JavaApplicationBuildModel> {
            @Inject
            public ApplyAction() {
            }

            @Inject
            protected abstract PluginManager getPluginManager();

            @Inject
            protected abstract TaskRegistrar getTaskRegistrar();

            @Inject
            protected abstract ProjectFeatureLayout getProjectFeatureLayout();

            @Inject
            protected abstract ProviderFactory getProviderFactory();

            @Inject
            protected abstract DependencyFactory getDependencyFactory();

            @Inject
            protected abstract Project getProject();

            @Inject
            protected abstract JavaToolchainService getJavaToolchainService();

            @Override
            public void apply(ProjectFeatureApplicationContext context, JvmApplication definition, JavaApplicationBuildModel buildModel) {
                getPluginManager().apply(ApplicationPlugin.class);
                CliExecutablesSupport.configureRunTasks(getTaskRegistrar(), buildModel);
                ((DefaultJavaApplicationBuildModel) buildModel).setJavaPluginExtension(
                        getProject().getExtensions().getByType(JavaPluginExtension.class)
                );
                ((DefaultJavaApplicationBuildModel) buildModel).setJavaApplicationExtension(
                        getProject().getExtensions().getByType(JavaApplication.class)
                );

                link(
                        definition,
                        buildModel,
                        getProject().getConfigurations(),
                        getProject().getTasks(),
                        getProjectFeatureLayout(),
                        getProviderFactory(),
                        getDependencyFactory(),
                        JavaPluginHelper.getJavaComponent(getProject()).getMainFeature().getSourceSet()
                );
            }

            private void link(
                    JvmApplication dslModel,
                    JavaApplicationBuildModel buildModel,
                    ConfigurationContainer configurations,
                    TaskContainer tasks,
                    ProjectFeatureLayout projectLayout,
                    ProviderFactory providers,
                    DependencyFactory dependencyFactory,
                    SourceSet commonSources) {

                JvmPluginSupport.setupCommonSourceSet(commonSources, projectLayout);
                JvmPluginSupport.linkSourceSetToDependencies(commonSources, dslModel.getDependencies(), configurations);

                JvmPluginSupport.linkJavaVersion(dslModel, buildModel.getJavaPluginExtension(), providers);
                JvmPluginSupport.linkApplicationMainClass(dslModel, buildModel.getJavaApplicationExtension());
                dslModel.getTargets().getStore().withType(JavaTarget.class).all(target -> {
                    SourceSet sourceSet = JvmPluginSupport.createTargetSourceSet(target, commonSources, getJavaToolchainService(), buildModel.getJavaPluginExtension(), tasks, configurations, dependencyFactory);

                    // Link dependencies to DSL
                    JvmPluginSupport.linkSourceSetToDependencies(sourceSet, target.getDependencies(), configurations);

                    // Create a run task
                    TaskProvider<JavaExec> runTask = tasks.register(sourceSet.getTaskName("run", null), JavaExec.class, task -> {
                        task.getMainClass().set(dslModel.getMainClass());
                        task.getJvmArguments().set(dslModel.getJvmArguments());
                        task.setClasspath(sourceSet.getRuntimeClasspath());
                    });
                    buildModel.getRunTasks().add(runTask);
                });
            }
        }
    }
}
