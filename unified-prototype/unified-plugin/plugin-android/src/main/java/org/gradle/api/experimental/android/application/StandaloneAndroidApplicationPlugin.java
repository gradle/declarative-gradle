package org.gradle.api.experimental.android.application;

import com.android.build.api.dsl.ApplicationBuildType;
import com.android.build.api.dsl.ApplicationExtension;
import com.android.build.api.dsl.BuildType;
import org.gradle.api.JavaVersion;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.android.DEFAULT_SDKS;
import org.gradle.api.experimental.common.ApplicationDependencies;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension;

import static org.gradle.api.experimental.android.AndroidDSLSupport.ifPresent;
import static org.gradle.api.experimental.android.AndroidDSLSupport.setContentTypeAttributes;

/**
 * Creates a declarative {@link AndroidApplication} DSL model, applies the official Android plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class StandaloneAndroidApplicationPlugin implements Plugin<Project> {
    @SoftwareType(name = "androidApplication", modelPublicType=AndroidApplication.class)
    abstract public AndroidApplication getAndroidApplication();

    @Override
    public void apply(Project project) {
        AndroidApplication dslModel = getAndroidApplication();

        // Setup Android Application conventions
        dslModel.getJdkVersion().convention(DEFAULT_SDKS.JDK);
        dslModel.getCompileSdk().convention(DEFAULT_SDKS.TARGET_ANDROID_SDK);
        dslModel.getMinSdk().convention(DEFAULT_SDKS.MIN_ANDROID_SDK); // https://developer.android.com/build/multidex#mdex-gradle
        dslModel.getBuildTypes().getDebug().getMinify().getEnabled().convention(false);
        dslModel.getBuildTypes().getRelease().getMinify().getEnabled().convention(false);

        // Enable desugaring automatically when JDK > 8 is targeted
        dslModel.getCoreLibraryDesugaring().getEnabled().convention(project.provider(() -> dslModel.getJdkVersion().get() > 8));
        dslModel.getCoreLibraryDesugaring().getLibVersion().convention("2.0.4");

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
            ifPresent(dslModel.getVersionCode(), defaultConfig::setVersionCode);
            ifPresent(dslModel.getVersionName(), defaultConfig::setVersionName);
            ifPresent(dslModel.getApplicationId(), defaultConfig::setApplicationId);
            return null;
        });
        android.compileOptions(compileOptions -> {
            // Up to Java 11 APIs are available through desugaring
            // https://developer.android.com/studio/write/java11-minimal-support-table
            compileOptions.setSourceCompatibility(JavaVersion.toVersion(dslModel.getJdkVersion().get()));
            compileOptions.setTargetCompatibility(JavaVersion.toVersion(dslModel.getJdkVersion().get()));
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

        // TODO: ProductFlavors are automatically added by the LIBRARY plugin via NiA support only, ATM, so we
        // need to make sure any Android APPLICATION projects have the necessary attributes for project deps to work.
        // TODO: Maybe there should be an AbstractAndroidPlugin that does this for all Android plugins?
        setContentTypeAttributes(project);

        setupDesugaring(project, dslModel, android);
    }

    private static void setupDesugaring(Project project, AndroidApplication dslModel, ApplicationExtension android) {
        android.compileOptions(compileOptions -> {
            compileOptions.setCoreLibraryDesugaringEnabled(dslModel.getCoreLibraryDesugaring().getEnabled().get());
            return null;
        });

        if (dslModel.getCoreLibraryDesugaring().getEnabled().get()) {
            project.getDependencies().addProvider("coreLibraryDesugaring", dslModel.getCoreLibraryDesugaring().getLibVersion().map(version -> "com.android.tools:desugar_jdk_libs:" + version));
        }
    }

    /**
     * Performs linking actions that do not need to occur within an afterEvaluate block.
     */
    public static void linkDslModelToPluginLazy(Project project, AndroidApplication dslModel) {
        ConfigurationContainer configurations = project.getConfigurations();
        linkCommonDependencies(dslModel.getDependencies(), configurations);
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void linkCommonDependencies(AndroidApplicationDependencies dependencies, ConfigurationContainer configurations) {
        configurations.getByName("implementation").fromDependencyCollector(dependencies.getImplementation());
        configurations.getByName("compileOnly").fromDependencyCollector(dependencies.getCompileOnly());
        configurations.getByName("runtimeOnly").fromDependencyCollector(dependencies.getRuntimeOnly());
    }

    /**
     * Links build types from the model to the android extension.
     */
    private static void linkBuildType(ApplicationBuildType android, AndroidApplicationBuildType model, ConfigurationContainer configurations) {
        android.setMinifyEnabled(model.getMinify().getEnabled().get());
        ifPresent(model.getVersionNameSuffix(), android::setVersionNameSuffix);
        ifPresent(model.getApplicationIdSuffix(), android::setApplicationIdSuffix);
        linkBuildTypeDependencies(android, model.getDependencies(), configurations);
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void linkBuildTypeDependencies(BuildType buildType, ApplicationDependencies dependencies, ConfigurationContainer configurations) {
        String name = buildType.getName();
        configurations.getByName(name + "Implementation").fromDependencyCollector(dependencies.getImplementation());
        configurations.getByName(name + "CompileOnly").fromDependencyCollector(dependencies.getCompileOnly());
        configurations.getByName(name + "RuntimeOnly").fromDependencyCollector(dependencies.getRuntimeOnly());
    }
}
