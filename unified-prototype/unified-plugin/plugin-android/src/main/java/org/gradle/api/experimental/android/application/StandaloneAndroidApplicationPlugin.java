package org.gradle.api.experimental.android.application;

import com.android.build.api.dsl.ApplicationExtension;
import org.gradle.api.Project;
import org.gradle.api.experimental.android.AbstractAndroidSoftwarePlugin;
import org.gradle.api.experimental.android.AndroidSoftware;
import org.gradle.api.experimental.android.extensions.linting.LintSupport;
import org.gradle.api.experimental.android.nia.NiaSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;

import static org.gradle.api.experimental.android.AndroidSupport.ifPresent;

/**
 * Creates a declarative {@link AndroidApplication} DSL model, applies the official Android plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class StandaloneAndroidApplicationPlugin extends AbstractAndroidSoftwarePlugin {

    public static final String ANDROID_APPLICATION = "androidApplication";

    @SoftwareType(name = ANDROID_APPLICATION, modelPublicType = AndroidApplication.class)
    public abstract AndroidApplication getAndroidApplication();

    @Override
    protected AndroidSoftware getAndroidSoftware() {
        return getAndroidApplication();
    }

    @Override
    public void apply(Project project) {
        super.apply(project);

        AndroidApplication dslModel = getAndroidApplication();

        // Setup application-specific conventions
        dslModel.getDependencyGuard().getEnabled().convention(false);

        dslModel.getFirebase().getEnabled().convention(false);
        dslModel.getFirebase().getVersion().convention("32.4.0");

        dslModel.getBuildTypes().getDebug().getApplicationIdSuffix().convention((String) null);
        dslModel.getBuildTypes().getRelease().getApplicationIdSuffix().convention((String) null);

        dslModel.getFlavors().getEnabled().convention(false);
        dslModel.getViewBinding().getEnabled().convention(false);
        dslModel.getDataBinding().getEnabled().convention(false);

        // Register an afterEvaluate listener before we apply the Android plugin to ensure we can
        // run actions before Android does.
        project.afterEvaluate(p -> linkDslModelToPlugin(p, dslModel));

        // Apply the official Android plugin.
        project.getPlugins().apply("com.android.application");
        project.getPlugins().apply("org.jetbrains.kotlin.android");

        // After AGP creates configurations, link deps to the collectors
        linkCommonDependencies(dslModel.getDependencies(), project.getConfigurations());
    }

    /**
     * Performs linking actions that must occur within an afterEvaluate block.
     */
    private void linkDslModelToPlugin(Project project, AndroidApplication dslModel) {
        ApplicationExtension android = project.getExtensions().getByType(ApplicationExtension.class);
        super.linkDslModelToPlugin(project, dslModel, android);

        android.defaultConfig(defaultConfig -> {
            ifPresent(dslModel.getVersionCode(), defaultConfig::setVersionCode);
            ifPresent(dslModel.getVersionName(), defaultConfig::setVersionName);
            ifPresent(dslModel.getApplicationId(), defaultConfig::setApplicationId);
            return null;
        });
        LintSupport.configureLint(project, dslModel);

        android.getBuildFeatures().setViewBinding(dslModel.getViewBinding().getEnabled().get());
        android.getBuildFeatures().setDataBinding(dslModel.getDataBinding().getEnabled().get());

        // TODO:DG All this configuration should be moved to the NiA project
        if (NiaSupport.isNiaProject(project)) {
            NiaSupport.configureNiaApplication(project, dslModel);
        }
    }
}
