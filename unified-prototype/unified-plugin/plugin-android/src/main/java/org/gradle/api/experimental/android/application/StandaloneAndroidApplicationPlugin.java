package org.gradle.api.experimental.android.application;

import com.android.build.api.dsl.ApplicationExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.android.AndroidBindingSupport;
import org.gradle.api.experimental.android.application.internal.DefaultAndroidApplicationBuildModel;
import org.gradle.api.experimental.android.extensions.linting.LintSupport;
import org.gradle.api.experimental.android.nia.NiaSupport;
import org.gradle.api.plugins.PluginManager;
import org.gradle.features.annotations.BindsProjectType;
import org.gradle.features.binding.ProjectTypeBinding;
import org.gradle.features.binding.ProjectTypeBindingBuilder;

import javax.inject.Inject;

import static org.gradle.api.experimental.android.AndroidSupport.ifPresent;

/**
 * Creates a declarative {@link AndroidApplication} DSL model, applies the official Android plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
@BindsProjectType(StandaloneAndroidApplicationPlugin.Binding.class)
public abstract class StandaloneAndroidApplicationPlugin implements Plugin<Project> {

    public static final String ANDROID_APPLICATION = "androidApplication";

    static class Binding implements ProjectTypeBinding {
        @Override
        public void bind(ProjectTypeBindingBuilder builder) {
            builder.bindProjectType(ANDROID_APPLICATION, AndroidApplication.class, (context, definition, buildModel) -> {
                Services services = context.getObjectFactory().newInstance(Services.class);

                // Apply the official Android plugin.
                services.getPluginManager().apply("com.android.application");

                // Add the legacy extension to the build model
                ((DefaultAndroidApplicationBuildModel)buildModel).setApplicationExtension(
                        services.getProject().getExtensions().getByType(ApplicationExtension.class)
                );

                // Configure android extension from the definition
                linkDefinitionToPlugin(services.getProject(), definition, buildModel, services.getProject().getConfigurations());
            })
            .withUnsafeDefinition()
            .withUnsafeApplyAction()
            .withBuildModelImplementationType(DefaultAndroidApplicationBuildModel.class);
        }

        /**
         * Performs linking actions that must occur within an afterEvaluate block.
         */
        private void linkDefinitionToPlugin(Project project, AndroidApplication definition, AndroidApplicationBuildModel buildModel, ConfigurationContainer configurations) {
            ApplicationExtension android = buildModel.getApplicationExtension();
            AndroidBindingSupport.linkCommonDependencies(definition.getDependencies(), configurations);
            AndroidBindingSupport.linkDefinitionToPlugin(project, definition, android);

            android.defaultConfig(defaultConfig -> {
                ifPresent(definition.getVersionCode(), defaultConfig::setVersionCode);
                ifPresent(definition.getVersionName(), defaultConfig::setVersionName);
                ifPresent(definition.getApplicationId(), defaultConfig::setApplicationId);
                return null;
            });
            LintSupport.configureLint(project, definition);

            android.getBuildFeatures().setViewBinding(definition.getViewBinding().getEnabled().get());
            android.getBuildFeatures().setDataBinding(definition.getDataBinding().getEnabled().get());

            // TODO:DG All this configuration should be moved to the NiA project
            if (NiaSupport.isNiaProject(project)) {
                NiaSupport.configureNiaApplication(project, definition);
            }
        }

        interface Services {
            @Inject
            PluginManager getPluginManager();

            @Inject
            Project getProject();
        }
    }

    @Override
    public void apply(Project project) { }
}
