package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.DependencyFactory;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.internal.plugins.BindsSoftwareType;
import org.gradle.api.internal.plugins.SoftwareTypeBindingBuilder;
import org.gradle.api.internal.plugins.SoftwareTypeBindingRegistration;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.internal.JavaPluginHelper;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.jvm.toolchain.JavaToolchainService;

import javax.inject.Inject;

/**
 * Creates a declarative {@link JvmLibrary} DSL model, applies the official Jvm plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
@BindsSoftwareType(StandaloneJvmLibraryPlugin.Binding.class)
public abstract class StandaloneJvmLibraryPlugin implements Plugin<Project> {

    public static final String JVM_LIBRARY = "jvmLibrary";

    @Override
    public void apply(Project project) {

    }

    static class Binding implements SoftwareTypeBindingRegistration {
        @Override
        public void register(SoftwareTypeBindingBuilder builder) {
            builder.bindSoftwareType(JVM_LIBRARY, JvmLibrary.class,
                    (context, definition, buildModel) -> {
                        Project project = context.getProject();
                        project.getPlugins().apply(JavaLibraryPlugin.class);
                        ((DefaultJavaBuildModel) buildModel).setJavaPluginExtension(
                                project.getExtensions().getByType(JavaPluginExtension.class)
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
            ).withBuildModelImplementationType(DefaultJavaBuildModel.class);
        }
    }

    static abstract class ModelToPluginLinker {
        @Inject
        public ModelToPluginLinker() {
        }

        @Inject
        protected abstract JavaToolchainService getJavaToolchainService();

        private void link(
                JvmLibrary dslModel,
                JavaBuildModel buildModel,
                ConfigurationContainer configurations,
                TaskContainer tasks,
                ProjectLayout projectLayout,
                ProviderFactory providers,
                DependencyFactory dependencyFactory,
                SourceSet commonSources) {

            JvmPluginSupport.setupCommonSourceSet(commonSources, projectLayout);
            JvmPluginSupport.linkSourceSetToDependencies(commonSources, dslModel.getDependencies(), configurations);

            JvmPluginSupport.linkJavaVersion(dslModel, buildModel.getJavaPluginExtension(), providers);

            dslModel.getTargets().withType(JavaTarget.class).all(target -> {
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
