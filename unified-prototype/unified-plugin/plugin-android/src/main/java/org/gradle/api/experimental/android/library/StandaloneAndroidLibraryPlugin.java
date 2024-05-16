package org.gradle.api.experimental.android.library;

import com.android.build.api.dsl.BuildType;
import com.android.build.api.dsl.LibraryBuildType;
import com.android.build.api.dsl.LibraryExtension;
import com.android.build.api.dsl.UnitTestOptions;
import org.gradle.api.JavaVersion;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.android.DEFAULT_SDKS;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension;
import org.gradle.api.experimental.android.nia.NiaSupport;

import static org.gradle.api.experimental.android.AndroidDSLSupport.ifPresent;

/**
 * Creates a declarative {@link AndroidLibrary} DSL model, applies the official Android plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class StandaloneAndroidLibraryPlugin implements Plugin<Project> {
    @SoftwareType(name = "androidLibrary", modelPublicType=AndroidLibrary.class)
    abstract public AndroidLibrary getAndroidLibrary();

    @Override
    public void apply(Project project) {
        AndroidLibrary dslModel = getAndroidLibrary();

        // Setup Android Library conventions
        dslModel.getJdkVersion().convention(DEFAULT_SDKS.JDK);
        dslModel.getCompileSdk().convention(DEFAULT_SDKS.TARGET_ANDROID_SDK);
        dslModel.getMinSdk().convention(DEFAULT_SDKS.MIN_ANDROID_SDK); // https://developer.android.com/build/multidex#mdex-gradle
        dslModel.getKotlinSerialization().getEnabled().convention(false);
        dslModel.getKotlinSerialization().getVersion().convention("1.6.3");
        dslModel.getFeature().getEnabled().convention(false);
        dslModel.getCompose().getEnabled().convention(false);

        // And Test Options
        dslModel.getTesting().getTestOptions().getIncludeAndroidResources().convention(false);
        dslModel.getTesting().getTestOptions().getReturnDefaultValues().convention(false);
        dslModel.getTesting().getJacoco().getEnabled().convention(false);
        dslModel.getTesting().getJacoco().getVersion().convention("0.8.7");

        // Register an afterEvaluate listener before we apply the Android plugin to ensure we can
        // run actions before Android does.
        project.afterEvaluate(p -> linkDslModelToPlugin(p, dslModel));

        // Apply the official Android plugin and support for Kotlin
        project.getPlugins().apply("com.android.library");
        project.getPlugins().apply("org.jetbrains.kotlin.android");

        // Add support for KSP
        project.getPlugins().apply("com.google.devtools.ksp");
        project.getDependencies().add("ksp", "com.google.dagger:hilt-android-compiler:2.50");

        // Add support for Hilt
        project.getPlugins().apply("dagger.hilt.android.plugin");
        project.getDependencies().add("implementation", "com.google.dagger:hilt-android:2.50");

        linkDslModelToPluginLazy(project, dslModel);
    }

    /**
     * Performs linking actions that must occur within an afterEvaluate block.
     */
    public static void linkDslModelToPlugin(Project project, AndroidLibrary dslModel) {
        LibraryExtension android = project.getExtensions().getByType(LibraryExtension.class);
        KotlinAndroidProjectExtension kotlin = project.getExtensions().getByType(KotlinAndroidProjectExtension.class);
        ConfigurationContainer configurations = project.getConfigurations();

        // Link common properties
        ifPresent(dslModel.getNamespace(), android::setNamespace);
        ifPresent(dslModel.getCompileSdk(), android::setCompileSdk);
        android.defaultConfig(defaultConfig -> {
            ifPresent(dslModel.getMinSdk(), defaultConfig::setMinSdk);
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
        NamedDomainObjectContainer<? extends LibraryBuildType> androidBuildTypes = android.getBuildTypes();
        AndroidLibraryBuildTypes modelBuildType = dslModel.getBuildTypes();
        linkBuildType(androidBuildTypes.getByName("debug"), modelBuildType.getDebug(), configurations);
        linkBuildType(androidBuildTypes.getByName("release"), modelBuildType.getRelease(), configurations);

        if (dslModel.getKotlinSerialization().getEnabled().get()) {
            configureKotlinSerialization(project, dslModel, configurations);
        }
        configureTesting(project, dslModel, android);

        NiaSupport.configureNia(project, dslModel);

        android.compileOptions(compileOptions -> {
            compileOptions.setCoreLibraryDesugaringEnabled(!dslModel.getDependencies().getCoreLibraryDesugaring().getDependencies().get().isEmpty());
            return null;
        });
    }

    private static void configureTesting(Project project, AndroidLibrary dslModel, LibraryExtension android) {
        Testing testing = dslModel.getTesting();
        AndroidTestDependencies testDependencies = testing.getDependencies();
        TestOptions testOptions = testing.getTestOptions();

        UnitTestOptions unitTestOptions = android.getTestOptions().getUnitTests();
        unitTestOptions.setIncludeAndroidResources(testOptions.getIncludeAndroidResources().get());
        unitTestOptions.setReturnDefaultValues(testOptions.getReturnDefaultValues().get());

        ConfigurationContainer configurations = project.getConfigurations();
        configurations.getByName("testImplementation").fromDependencyCollector(testDependencies.getImplementation());
        configurations.getByName("androidTestImplementation").fromDependencyCollector(testDependencies.getAndroidImplementation());
    }

    private static void configureKotlinSerialization(Project project, AndroidLibrary dslModel, ConfigurationContainer configurations) {
        project.getPlugins().apply("org.jetbrains.kotlin.plugin.serialization");
        configurations.getByName("testImplementation").fromDependencyCollector(dslModel.getKotlinSerialization().getDependencies().getImplementation());
    }

    /**
     * Performs linking actions that do not need to occur within an afterEvaluate block.
     */
    public static void linkDslModelToPluginLazy(Project project, AndroidLibrary dslModel) {
        ConfigurationContainer configurations = project.getConfigurations();
        linkCommonDependencies(dslModel.getDependencies(), configurations);
    }

    private static void linkCommonDependencies(AndroidLibraryDependencies dependencies, ConfigurationContainer configurations) {
        configurations.getByName("implementation").fromDependencyCollector(dependencies.getImplementation());
        configurations.getByName("api").fromDependencyCollector(dependencies.getApi());
        configurations.getByName("compileOnly").fromDependencyCollector(dependencies.getCompileOnly());
        configurations.getByName("runtimeOnly").fromDependencyCollector(dependencies.getRuntimeOnly());
        configurations.getByName("ksp").fromDependencyCollector(dependencies.getKsp());
        configurations.getByName("coreLibraryDesugaring").fromDependencyCollector(dependencies.getCoreLibraryDesugaring());
    }

    /**
     * Links build types from the model to the android extension.
     */
    private static void linkBuildType(LibraryBuildType buildType, AndroidLibraryBuildType model, ConfigurationContainer configurations) {
        ifPresent(model.getMinifyEnabled(), buildType::setMinifyEnabled);
        linkBuildTypeDependencies(buildType, model.getDependencies(), configurations);
    }

    private static void linkBuildTypeDependencies(BuildType buildType, AndroidLibraryDependencies dependencies, ConfigurationContainer configurations) {
        String name = buildType.getName();
        configurations.getByName(name + "Implementation").fromDependencyCollector(dependencies.getImplementation());
        configurations.getByName(name + "Api").fromDependencyCollector(dependencies.getApi());
        configurations.getByName(name + "CompileOnly").fromDependencyCollector(dependencies.getCompileOnly());
        configurations.getByName(name + "RuntimeOnly").fromDependencyCollector(dependencies.getRuntimeOnly());
    }
}
