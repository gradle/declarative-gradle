package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.DependencyFactory;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.plugins.internal.JavaPluginHelper;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.features.annotations.BindsProjectType;
import org.gradle.features.binding.ProjectFeatureApplicationContext;
import org.gradle.features.binding.ProjectTypeApplyAction;
import org.gradle.features.binding.ProjectTypeBinding;
import org.gradle.features.binding.ProjectTypeBindingBuilder;
import org.gradle.features.file.ProjectFeatureLayout;
import org.gradle.features.registration.ConfigurationRegistrar;
import org.gradle.jvm.toolchain.JavaToolchainService;

import javax.inject.Inject;

/**
 * Creates a declarative {@link JvmLibrary} DSL model, applies the official Jvm plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
@BindsProjectType(StandaloneJvmLibraryPlugin.Binding.class)
public abstract class StandaloneJvmLibraryPlugin implements Plugin<Project> {

    public static final String JVM_LIBRARY = "jvmLibrary";

    @Override
    public void apply(Project project) {

    }

    static class Binding implements ProjectTypeBinding {
        @Override
        public void bind(ProjectTypeBindingBuilder builder) {
            builder.bindProjectType(JVM_LIBRARY, JvmLibrary.class, ApplyAction.class)
                    .withUnsafeDefinition()
                    .withUnsafeApplyAction()
                    .withBuildModelImplementationType(DefaultJavaBuildModel.class);
        }

        @SuppressWarnings("UnstableApiUsage")
        static abstract class ApplyAction implements ProjectTypeApplyAction<JvmLibrary, JavaBuildModel> {
            @Inject
            public ApplyAction() {
            }

            @Inject
            protected abstract PluginManager getPluginManager();

            @Inject
            protected abstract ConfigurationRegistrar getConfigurationRegistrar();

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
            public void apply(ProjectFeatureApplicationContext context, JvmLibrary definition, JavaBuildModel buildModel) {
                getPluginManager().apply(JavaLibraryPlugin.class);
                ((DefaultJavaBuildModel) buildModel).setJavaPluginExtension(
                        getProject().getExtensions().getByType(JavaPluginExtension.class)
                );

                link(
                        definition,
                        buildModel,
                        getConfigurationRegistrar(),
                        getProject().getConfigurations(),
                        getProject().getTasks(),
                        getProjectFeatureLayout(),
                        getProviderFactory(),
                        getDependencyFactory(),
                        JavaPluginHelper.getJavaComponent(getProject()).getMainFeature().getSourceSet()
                );
            }

            private void link(
                    JvmLibrary dslModel,
                    JavaBuildModel buildModel,
                    ConfigurationRegistrar configurationRegistrar,
                    ConfigurationContainer configurations,
                    TaskContainer tasks,
                    ProjectFeatureLayout projectLayout,
                    ProviderFactory providers,
                    DependencyFactory dependencyFactory,
                    SourceSet commonSources) {

                JvmPluginSupport.setupCommonSourceSet(commonSources, projectLayout);
                JvmPluginSupport.linkSourceSetToDependencies(commonSources, dslModel.getDependencies(), configurations);

                JvmPluginSupport.linkJavaVersion(dslModel, buildModel.getJavaPluginExtension(), providers);

                dslModel.getTargets().getStore().withType(JavaTarget.class).all(target -> {
                    SourceSet sourceSet = JvmPluginSupport.createTargetSourceSet(target, commonSources, getJavaToolchainService(), buildModel.getJavaPluginExtension(), tasks, configurations, dependencyFactory);

                    // Link dependencies to DSL
                    JvmPluginSupport.linkSourceSetToDependencies(sourceSet, target.getDependencies(), configurations);

                    // Extend common dependencies
                    configurations.getByName(sourceSet.getApiConfigurationName())
                            .extendsFrom(configurations.getByName(commonSources.getApiConfigurationName()));
                });
            }
        }
    }
}
