package org.gradle.api.experimental.android.application;

import com.android.build.api.dsl.ApplicationBuildType;
import com.android.build.api.dsl.ApplicationExtension;
import com.android.build.api.dsl.BuildType;
import org.gradle.api.JavaVersion;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.common.ApplicationDependencies;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension;

import static org.gradle.api.experimental.android.AndroidDSLSupport.ifPresent;

/**
 * Creates a declarative {@link AndroidApplication} DSL model, applies the official Android plugin,
 * and links the declarative model to the official plugin.
 */
public abstract class StandaloneAndroidApplicationPlugin implements Plugin<Project> {
    @SoftwareType(name = "androidApplication", modelPublicType=AndroidApplication.class)
    abstract public AndroidApplication getAndroidApplication();

    @Override
    public void apply(Project project) {
        AndroidApplication dslModel = getAndroidApplication();

        // Register an afterEvaluate listener before we apply the Android plugin to ensure we can
        // run actions before Android does.
        project.afterEvaluate(p -> linkDslModelToPlugin(p, dslModel));

        // Apply the official Android plugin.
        project.getPlugins().apply("com.android.application");
        project.getPlugins().apply("org.jetbrains.kotlin.android");

        linkDslModelToPluginLazy(project, dslModel);
    }

    /**
     * Performs linking actions that must occur within an afterEvaluate block.
     */
    public static void linkDslModelToPlugin(Project project, AndroidApplication dslModel) {
        ApplicationExtension android = project.getExtensions().getByType(ApplicationExtension.class);
        KotlinAndroidProjectExtension kotlin = project.getExtensions().getByType(KotlinAndroidProjectExtension.class);
        ConfigurationContainer configurations = project.getConfigurations();

        // Link common properties
        ifPresent(dslModel.getNamespace(), android::setNamespace);
        ifPresent(dslModel.getCompileSdk(), android::setCompileSdk);
        android.defaultConfig(defaultConfig -> {
            ifPresent(dslModel.getMinSdk(), defaultConfig::setMinSdk);
            return null;
        });
        ifPresent(dslModel.getJdkVersion(), jdkVersion -> {
            kotlin.jvmToolchain(jdkVersion);
            android.getCompileOptions().setSourceCompatibility(JavaVersion.toVersion(jdkVersion));
            android.getCompileOptions().setTargetCompatibility(JavaVersion.toVersion(jdkVersion));
        });

        // Link build types
        NamedDomainObjectContainer<? extends ApplicationBuildType> androidBuildTypes = android.getBuildTypes();
        AndroidApplicationBuildTypes modelBuildType = dslModel.getBuildTypes();
        linkBuildType(androidBuildTypes.getByName("debug"), modelBuildType.getDebug(), configurations);
        linkBuildType(androidBuildTypes.getByName("release"), modelBuildType.getRelease(), configurations);
    }

    /**
     * Performs linking actions that do not need to occur within an afterEvaluate block.
     */
    public static void linkDslModelToPluginLazy(Project project, AndroidApplication dslModel) {
        ConfigurationContainer configurations = project.getConfigurations();
        linkCommonDependencies(dslModel.getDependencies(), configurations);
    }

    private static void linkCommonDependencies(ApplicationDependencies dependencies, ConfigurationContainer configurations) {
        configurations.getByName("implementation").fromDependencyCollector(dependencies.getImplementation());
        configurations.getByName("compileOnly").fromDependencyCollector(dependencies.getCompileOnly());
        configurations.getByName("runtimeOnly").fromDependencyCollector(dependencies.getRuntimeOnly());
    }

    /**
     * Links build types from the model to the android extension.
     */
    private static void linkBuildType(ApplicationBuildType android, AndroidApplicationBuildType model, ConfigurationContainer configurations) {
        ifPresent(model.getMinifyEnabled(), android::setMinifyEnabled);
        ifPresent(model.getVersionNameSuffix(), android::setVersionNameSuffix);
        ifPresent(model.getApplicationIdSuffix(), android::setApplicationIdSuffix);
        linkBuildTypeDependencies(android, model.getDependencies(), configurations);
    }

    private static void linkBuildTypeDependencies(BuildType buildType, ApplicationDependencies dependencies, ConfigurationContainer configurations) {
        String name = buildType.getName();
        configurations.getByName(name + "Implementation").fromDependencyCollector(dependencies.getImplementation());
        configurations.getByName(name + "CompileOnly").fromDependencyCollector(dependencies.getCompileOnly());
        configurations.getByName(name + "RuntimeOnly").fromDependencyCollector(dependencies.getRuntimeOnly());
    }
}
