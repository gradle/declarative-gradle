package org.gradle.api.experimental.android.library;

import com.android.build.api.dsl.BuildType;
import com.android.build.api.dsl.LibraryExtension;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.android.AbstractAndroidSoftwarePlugin;
import org.gradle.api.experimental.android.AndroidSoftware;
import org.gradle.api.experimental.android.nia.NiaSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;

/**
 * Creates a declarative {@link AndroidLibrary} DSL model, applies the official Android plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class StandaloneAndroidLibraryPlugin extends AbstractAndroidSoftwarePlugin {
    @SoftwareType(name = "androidLibrary", modelPublicType=AndroidLibrary.class)
    public abstract AndroidLibrary getAndroidLibrary();

    @Override
    protected AndroidSoftware getAndroidSoftware() {
        return getAndroidLibrary();
    }

    @Override
    public void apply(Project project) {
        super.apply(project);

        AndroidLibrary dslModel = getAndroidLibrary();

        // Register an afterEvaluate listener before we apply the Android plugin to ensure we can
        // run actions before Android does.
        project.afterEvaluate(p -> linkDslModelToPlugin(p, dslModel));

        // Apply the official Android plugin and support for Kotlin
        project.getPlugins().apply("com.android.library");
        project.getPlugins().apply("org.jetbrains.kotlin.android");

        // After AGP creates configurations, link deps to the collectors
        linkCommonDependencies(dslModel.getDependencies(), project.getConfigurations());
    }

    /**
     * Performs linking actions that must occur within an afterEvaluate block.
     */
    private void linkDslModelToPlugin(Project project, AndroidLibrary dslModel) {
        LibraryExtension android = project.getExtensions().getByType(LibraryExtension.class);
        super.linkDslModelToPlugin(project, dslModel, android);

        // TODO: All this configuration should be moved to the NiA project
        if (NiaSupport.isNiaProject(project)) {
            NiaSupport.configureNiaLibrary(project, dslModel);
        }

        ifPresent(dslModel.getConsumerProguardFile(), android.getDefaultConfig()::consumerProguardFile);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void linkCommonDependencies(AndroidLibraryDependencies dependencies, ConfigurationContainer configurations) {
        super.linkCommonDependencies(dependencies, configurations);
        configurations.getByName("api").fromDependencyCollector(dependencies.getApi()); // API deps added for libraries
    }

    private void linkBuildTypeDependencies(BuildType buildType, AndroidLibraryDependencies dependencies, ConfigurationContainer configurations) {
        super.linkBuildTypeDependencies(buildType, dependencies, configurations);
        String name = buildType.getName();
        configurations.getByName(name + "Api").fromDependencyCollector(dependencies.getApi());
    }
}
