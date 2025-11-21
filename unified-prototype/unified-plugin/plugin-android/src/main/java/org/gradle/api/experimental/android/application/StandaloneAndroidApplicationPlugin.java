package org.gradle.api.experimental.android.application;

import com.android.build.api.dsl.ApplicationExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.android.AndroidBindingSupport;
import org.gradle.api.experimental.android.application.internal.DefaultAndroidApplicationBuildModel;
import org.gradle.api.experimental.android.extensions.linting.LintSupport;
import org.gradle.api.experimental.android.nia.NiaSupport;
import org.gradle.api.internal.plugins.BindsProjectType;
import org.gradle.api.internal.plugins.ProjectTypeBinding;
import org.gradle.api.internal.plugins.ProjectTypeBindingBuilder;

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
                AndroidBindingSupport.bindCommon(context, definition);

                // Setup application-specific conventions
                definition.getDependencyGuard().getEnabled().convention(false);

                definition.getFirebase().getEnabled().convention(false);
                definition.getFirebase().getVersion().convention("32.4.0");

                definition.getBuildTypes().getDebug().getApplicationIdSuffix().convention((String) null);
                definition.getBuildTypes().getRelease().getApplicationIdSuffix().convention((String) null);

                definition.getFlavors().getEnabled().convention(false);
                definition.getViewBinding().getEnabled().convention(false);
                definition.getDataBinding().getEnabled().convention(false);

                // Register an afterEvaluate listener before we apply the Android plugin to ensure we can
                // run actions before Android does.
                context.getProject().afterEvaluate(p -> linkDefinitionToPlugin(p, definition, buildModel));

                // Apply the official Android plugin.
                context.getProject().getPlugins().apply("com.android.application");
                context.getProject().getPlugins().apply("org.jetbrains.kotlin.android");

                ((DefaultAndroidApplicationBuildModel)buildModel).setApplicationExtension(
                        context.getProject().getExtensions().getByType(ApplicationExtension.class)
                );

                // After AGP creates configurations, link deps to the collectors
                AndroidBindingSupport.linkCommonDependencies(definition.getDependencies(), context.getProject().getConfigurations());
            })
            .withBuildModelImplementationType(DefaultAndroidApplicationBuildModel.class);
        }

        /**
         * Performs linking actions that must occur within an afterEvaluate block.
         */
        private void linkDefinitionToPlugin(Project project, AndroidApplication definition, AndroidApplicationBuildModel buildModel) {
            ApplicationExtension android = buildModel.getApplicationExtension();
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
    }

    @Override
    public void apply(Project project) { }
}
