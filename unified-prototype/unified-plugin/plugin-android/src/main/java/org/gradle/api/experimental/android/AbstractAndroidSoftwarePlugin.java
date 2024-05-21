package org.gradle.api.experimental.android;

import com.android.build.api.dsl.*;
import org.gradle.api.*;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.android.extensions.testing.AndroidTestDependencies;
import org.gradle.api.experimental.android.extensions.testing.TestOptions;
import org.gradle.api.experimental.android.extensions.testing.Testing;
import org.gradle.api.experimental.android.nia.NiaSupport;
import org.gradle.api.provider.Property;
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension;

import java.util.Objects;

import static org.gradle.api.experimental.android.extensions.ComposeSupport.configureCompose;

public abstract class AbstractAndroidSoftwarePlugin implements Plugin<Project>  {
    protected static final int DEFAULT_JDK = 11;
    protected static final int DEFAULT_TARGET_ANDROID_SDK = 34;
    protected static final int DEFAULT_MIN_ANDROID_SDK = 21;

    protected abstract AndroidSoftware getAndroidSoftware();

    public void apply(Project project) {
        AndroidSoftware dslModel = getAndroidSoftware();

        // Setup Android software conventions
        dslModel.getJdkVersion().convention(DEFAULT_JDK);
        dslModel.getCompileSdk().convention(DEFAULT_TARGET_ANDROID_SDK);
        dslModel.getMinSdk().convention(DEFAULT_MIN_ANDROID_SDK); // https://developer.android.com/build/multidex#mdex-gradle

        // Setup minify conventions
        dslModel.getBuildTypes().getDebug().getMinify().getEnabled().convention(false);
        dslModel.getBuildTypes().getRelease().getMinify().getEnabled().convention(false);

        // Setup desugaring conventions and desugar automatically when JDK > 8 is targeted
        dslModel.getCoreLibraryDesugaring().getEnabled().convention(project.provider(() -> dslModel.getJdkVersion().get() > 8));
        dslModel.getCoreLibraryDesugaring().getLibVersion().convention("2.0.4");

        // Setup Serialization conventions
        dslModel.getKotlinSerialization().getEnabled().convention(false);
        dslModel.getKotlinSerialization().getVersion().convention("1.6.3");
        dslModel.getKotlinSerialization().getJsonEnabled().convention(false);

        // Setup other feature extension conventions
        dslModel.getFeature().getEnabled().convention(false);
        dslModel.getCompose().getEnabled().convention(false);
        dslModel.getHilt().getEnabled().convention(false);

        // Setup Test Options conventions
        dslModel.getTesting().getTestOptions().getIncludeAndroidResources().convention(false);
        dslModel.getTesting().getTestOptions().getReturnDefaultValues().convention(false);
        dslModel.getTesting().getJacoco().getEnabled().convention(false);
        dslModel.getTesting().getJacoco().getVersion().convention("0.8.7");
    }

    /**
     * Performs common dependency linking actions that do not need to occur within an afterEvaluate block.
     */
    @SuppressWarnings("UnstableApiUsage")
    protected void linkCommonDependencies(AndroidSoftwareDependencies dependencies, ConfigurationContainer configurations) {
        configurations.getByName("implementation").fromDependencyCollector(dependencies.getImplementation());
        configurations.getByName("compileOnly").fromDependencyCollector(dependencies.getCompileOnly());
        configurations.getByName("runtimeOnly").fromDependencyCollector(dependencies.getRuntimeOnly());
    }

    /**
     * Performs linking actions that must occur within an afterEvaluate block.
     */
    protected void linkDslModelToPlugin(Project project, AndroidSoftware dslModel, CommonExtension<?, ?, ?, ?, ?, ?> android) {
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
        AndroidSoftwareBuildTypes modelBuildType = dslModel.getBuildTypes();
        NamedDomainObjectContainer<? extends BuildType> androidBuildTypes = android.getBuildTypes();
        linkBuildType(androidBuildTypes.getByName("debug"), modelBuildType.getDebug(), configurations);
        linkBuildType(androidBuildTypes.getByName("release"), modelBuildType.getRelease(), configurations);

        configureTesting(project, dslModel, android);

        configureKotlinSerialization(project, dslModel);
        configureDesugaring(project, dslModel, android);
        configureHilt(project, dslModel, android);
        configureCompose(project, dslModel, android);

        // TODO: All this configuration should be moved to the NiA project
        if (Objects.equals(project.getRootProject().getName(), "nowinandroid")) {
            NiaSupport.configureNia(project, dslModel);
        }
    }

    protected void configureHilt(Project project, AndroidSoftware dslModel, CommonExtension<?, ?, ?, ?, ?, ?> android) {
        if (dslModel.getHilt().getEnabled().get()) {
            // Add support for KSP
            project.getPlugins().apply("com.google.devtools.ksp");
            project.getDependencies().add("ksp", "com.google.dagger:hilt-android-compiler:2.50");

            // Add support for Hilt
            project.getPlugins().apply("dagger.hilt.android.plugin");
            project.getDependencies().add("implementation", "com.google.dagger:hilt-android:2.50");
        }
    }

    protected void configureDesugaring(Project project, AndroidSoftware dslModel, CommonExtension<?, ?, ?, ?, ?, ?> android) {
        if (dslModel.getCoreLibraryDesugaring().getEnabled().get()) {
            android.compileOptions(compileOptions -> {
                compileOptions.setCoreLibraryDesugaringEnabled(dslModel.getCoreLibraryDesugaring().getEnabled().get());
                return null;
            });

            project.getDependencies().addProvider("coreLibraryDesugaring", dslModel.getCoreLibraryDesugaring().getLibVersion().map(version -> "com.android.tools:desugar_jdk_libs:" + version));
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    protected void configureTesting(Project project, AndroidSoftware dslModel, CommonExtension<?, ?, ?, ?, ?, ?> android) {
        Testing testing = dslModel.getTesting();
        AndroidTestDependencies testDependencies = testing.getDependencies();

        TestOptions testOptions = testing.getTestOptions();
        ifPresent(testOptions.getTestInstrumentationRunner(), android.getDefaultConfig()::setTestInstrumentationRunner);

        UnitTestOptions unitTestOptions = android.getTestOptions().getUnitTests();
        unitTestOptions.setIncludeAndroidResources(testOptions.getIncludeAndroidResources().get());
        unitTestOptions.setReturnDefaultValues(testOptions.getReturnDefaultValues().get());

        ConfigurationContainer configurations = project.getConfigurations();
        configurations.getByName("testImplementation").fromDependencyCollector(testDependencies.getImplementation());
        configurations.getByName("androidTestImplementation").fromDependencyCollector(testDependencies.getAndroidImplementation());
    }

    @SuppressWarnings("UnstableApiUsage")
    protected void configureKotlinSerialization(Project project, AndroidSoftware dslModel) {
        if (dslModel.getKotlinSerialization().getEnabled().get()) {
            project.getPlugins().apply("org.jetbrains.kotlin.plugin.serialization");
            project.getConfigurations().getByName("testImplementation").fromDependencyCollector(dslModel.getKotlinSerialization().getDependencies().getImplementation());

            if (dslModel.getKotlinSerialization().getJsonEnabled().get()) {
                project.getDependencies().addProvider("implementation", dslModel.getKotlinSerialization().getVersion().map(version -> "org.jetbrains.kotlinx:kotlinx-serialization-json:" + version));
            }
        }
    }

    /**
     * Links build types from the model to the android extension.
     */
    protected void linkBuildType(BuildType buildType, AndroidSoftwareBuildType model, ConfigurationContainer configurations) {
        buildType.setMinifyEnabled(model.getMinify().getEnabled().get());
        linkBuildTypeDependencies(buildType, model.getDependencies(), configurations);
    }

    @SuppressWarnings("UnstableApiUsage")
    protected void linkBuildTypeDependencies(BuildType buildType, AndroidSoftwareDependencies dependencies, ConfigurationContainer configurations) {
        String name = buildType.getName();
        configurations.getByName(name + "Implementation").fromDependencyCollector(dependencies.getImplementation());
        configurations.getByName(name + "CompileOnly").fromDependencyCollector(dependencies.getCompileOnly());
        configurations.getByName(name + "RuntimeOnly").fromDependencyCollector(dependencies.getRuntimeOnly());
    }


    protected <T> void ifPresent(Property<T> property, Action<T> action) {
        if (property.isPresent()) {
            action.execute(property.get());
        }
    }
}
