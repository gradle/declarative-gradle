package org.gradle.api.experimental.android;

import androidx.baselineprofile.gradle.consumer.BaselineProfileConsumerExtension;
import androidx.baselineprofile.gradle.producer.BaselineProfileProducerExtension;
import androidx.room.gradle.RoomExtension;
import com.android.build.api.dsl.BuildType;
import com.android.build.api.dsl.CommonExtension;
import com.android.build.api.dsl.ManagedVirtualDevice;
import com.android.build.api.dsl.UnitTestOptions;
import com.google.android.libraries.mapsplatform.secrets_gradle_plugin.SecretsPluginExtension;
import com.google.devtools.ksp.gradle.KspExtension;
import org.gradle.api.JavaVersion;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.android.extensions.testing.AndroidTestDependencies;
import org.gradle.api.experimental.android.extensions.testing.TestOptions;
import org.gradle.api.experimental.android.extensions.testing.Testing;
import org.gradle.api.internal.plugins.ProjectFeatureApplicationContext;
import org.gradle.api.tasks.testing.Test;
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension;

import java.io.File;

import static org.gradle.api.experimental.android.AndroidSupport.ifPresent;
import static org.gradle.api.experimental.android.extensions.ComposeSupport.configureCompose;
import static org.gradle.api.experimental.android.nia.NiaSupport.configureBaselineProfile;

@SuppressWarnings("UnstableApiUsage")
public abstract class AndroidBindingSupport {
    public static final int DEFAULT_MIN_ANDROID_SDK = 21;

    public static void bindCommon(ProjectFeatureApplicationContext context, AndroidSoftware definition) {
        // Setup Android software conventions
        definition.getMinSdk().convention(DEFAULT_MIN_ANDROID_SDK); // https://developer.android.com/build/multidex#mdex-gradle
        definition.getVectorDrawablesUseSupportLibrary().convention(false);

        // Setup minify conventions
        definition.getBuildTypes().getDebug().getMinify().getEnabled().convention(false);
        definition.getBuildTypes().getDebug().getBaselineProfile().getEnabled().convention(false);
        definition.getBuildTypes().getRelease().getMinify().getEnabled().convention(false);
        definition.getBuildTypes().getRelease().getBaselineProfile().getEnabled().convention(false);

        // Setup desugaring conventions and desugar automatically when JDK > 8 is targeted
        definition.getCoreLibraryDesugaring().getEnabled().convention(context.getProject().provider(() -> definition.getJdkVersion().get() > 8));
        definition.getCoreLibraryDesugaring().getLibVersion().convention("2.0.4");

        // Setup Serialization conventions
        definition.getKotlinSerialization().getEnabled().convention(false);
        definition.getKotlinSerialization().getVersion().convention("1.6.3");
        definition.getKotlinSerialization().getJsonEnabled().convention(false);

        // Setup Linting conventions
        definition.getLint().getEnabled().convention(false);
        definition.getLint().getXmlReport().convention(false);
        definition.getLint().getCheckDependencies().convention(false);

        // Setup other feature extension conventions
        definition.getFeature().getEnabled().convention(false);
        definition.getCompose().getEnabled().convention(false);
        definition.getHilt().getEnabled().convention(false);
        definition.getRoom().getEnabled().convention(false);
        definition.getRoom().getVersion().convention("2.6.1");
        definition.getLicenses().getEnabled().convention(false);
        definition.getBaselineProfile().getEnabled().convention(false);
        definition.getBaselineProfile().getUseConnectedDevices().convention(true);
        definition.getSecrets().getEnabled().convention(false);

        // Setup Test Options conventions
        definition.getTesting().getTestOptions().getIncludeAndroidResources().convention(false);
        definition.getTesting().getTestOptions().getReturnDefaultValues().convention(false);
        definition.getTesting().getJacoco().getEnabled().convention(false);
        definition.getTesting().getJacoco().getVersion().convention("0.8.7");
        definition.getTesting().getRoborazzi().getEnabled().convention(false);
        definition.getTesting().getFailOnNoDiscoveredTests().convention(true);
    }

    /**
     * Performs common dependency linking actions that do not need to occur within an afterEvaluate block.
     */
    @SuppressWarnings("UnstableApiUsage")
    public static void linkCommonDependencies(AndroidSoftwareDependencies dependencies, ConfigurationContainer configurations) {
        configurations.getByName("implementation").fromDependencyCollector(dependencies.getImplementation());
        configurations.getByName("compileOnly").fromDependencyCollector(dependencies.getCompileOnly());
        configurations.getByName("runtimeOnly").fromDependencyCollector(dependencies.getRuntimeOnly());
        configurations.getByName("lintChecks").fromDependencyCollector(dependencies.getLintChecks());
        configurations.getByName("lintPublish").fromDependencyCollector(dependencies.getLintPublish());
    }

    /**
     * Performs linking actions that must occur within an afterEvaluate block.
     */
    public static void linkDefinitionToPlugin(Project project, AndroidSoftware definition, CommonExtension<?, ?, ?, ?, ?, ?> android) {
        KotlinAndroidProjectExtension kotlin = project.getExtensions().getByType(KotlinAndroidProjectExtension.class);

        // Link common properties
        ifPresent(definition.getNamespace(), android::setNamespace);
        ifPresent(definition.getCompileSdk(), android::setCompileSdk);
        android.defaultConfig(defaultConfig -> {
            ifPresent(definition.getMinSdk(), defaultConfig::setMinSdk);
            ifPresent(definition.getVectorDrawablesUseSupportLibrary(), defaultConfig.getVectorDrawables()::setUseSupportLibrary);
            return null;
        });
        android.compileOptions(compileOptions -> {
            // Up to Java 11 APIs are available through desugaring
            // https://developer.android.com/studio/write/java11-minimal-support-table
            compileOptions.setSourceCompatibility(JavaVersion.toVersion(definition.getJdkVersion().get()));
            compileOptions.setTargetCompatibility(JavaVersion.toVersion(definition.getJdkVersion().get()));
            return null;
        });
        ifPresent(definition.getJdkVersion(), jdkVersion -> {
            kotlin.jvmToolchain(jdkVersion);
            android.getCompileOptions().setSourceCompatibility(JavaVersion.toVersion(jdkVersion));
            android.getCompileOptions().setTargetCompatibility(JavaVersion.toVersion(jdkVersion));
        });
        definition.getExperimentalProperties().forEach(property -> {
            android.getExperimentalProperties().put(property.getName(), property.getValue());
        });

        // Link build types
        AndroidSoftwareBuildTypes modelBuildType = definition.getBuildTypes();
        NamedDomainObjectContainer<? extends BuildType> androidBuildTypes = android.getBuildTypes();
        linkBuildType(project, androidBuildTypes.getByName("debug"), modelBuildType.getDebug(), android);
        linkBuildType(project, androidBuildTypes.getByName("release"), modelBuildType.getRelease(), android);

        configureTesting(project, definition, android);

        configureKotlinSerialization(project, definition);
        configureDesugaring(project, definition, android);
        configureHilt(project, definition);
        configureCompose(project, definition, android);
        configureRoom(project, definition);
        configureLicenses(project, definition);

        if (project.getExtensions().findByName("baselineProfile") != null) {
            BaselineProfileProducerExtension baselineProfileProducerExtension = project.getExtensions().getByType(BaselineProfileProducerExtension.class);
            BaselineProfileConsumerExtension baselineProfileConsumerExtension = project.getExtensions().getByType(BaselineProfileConsumerExtension.class);
            configureBaselineProfile(project, definition.getBaselineProfile(), baselineProfileProducerExtension, baselineProfileConsumerExtension);
        }

        configureSecrets(project, definition);
    }

    public static void configureSecrets(Project project, AndroidSoftware dslModel) {
        if (dslModel.getSecrets().getEnabled().get()) {
            project.getPlugins().apply("com.google.android.libraries.mapsplatform.secrets-gradle-plugin");

            SecretsPluginExtension secrets = project.getExtensions().getByType(SecretsPluginExtension.class);
            ifPresent(dslModel.getSecrets().getDefaultPropertiesFile(), file -> secrets.setDefaultPropertiesFileName(file.getAsFile().getName()));
        }
    }

    public static void configureLicenses(Project project, AndroidSoftware dslModel) {
        if (dslModel.getLicenses().getEnabled().get()) {
            project.getPlugins().apply("com.google.android.gms.oss-licenses-plugin");
        }
    }

    public static void configureHilt(Project project, AndroidSoftware dslModel) {
        if (dslModel.getHilt().getEnabled().get()) {
            project.getLogger().info("Hilt is enabled in: " + project.getPath());

            // Add support for KSP
            project.getPlugins().apply("com.google.devtools.ksp");
            project.getDependencies().add("ksp", "com.google.dagger:hilt-android-compiler:2.52");

            // Add support for Hilt
            project.getPlugins().apply("dagger.hilt.android.plugin");
            project.getDependencies().add("implementation", "com.google.dagger:hilt-android:2.52");

            project.getDependencies().add("kspTest", "com.google.dagger:hilt-android-compiler:2.52");
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void configureRoom(Project project, AndroidSoftware dslModel) {
        if (dslModel.getRoom().getEnabled().get()) {
            project.getLogger().info("Room is enabled in: " + project.getPath());

            project.getPlugins().apply("androidx.room");
            project.getPlugins().apply("com.google.devtools.ksp");

            KspExtension kspExtension = project.getExtensions().getByType(KspExtension.class);
            kspExtension.arg("room.generateKotlin", "true");

            RoomExtension room = project.getExtensions().getByType(RoomExtension.class);
            // The schemas directory contains a schema file for each version of the Room database.
            // This is required to enable Room auto migrations.
            // See https://developer.android.com/reference/kotlin/androidx/room/AutoMigration.
            ifPresent(dslModel.getRoom().getSchemaDirectory(), room::schemaDirectory);

            String roomVersion = dslModel.getRoom().getVersion().get();
            dslModel.getDependencies().getImplementation().add("androidx.room:room-runtime:" + roomVersion);
            dslModel.getDependencies().getImplementation().add("androidx.room:room-ktx:" + roomVersion);
            project.getDependencies().add("ksp", "androidx.room:room-compiler:" + roomVersion);
        }
    }

    public static void configureDesugaring(Project project, AndroidSoftware dslModel, CommonExtension<?, ?, ?, ?, ?, ?> android) {
        if (dslModel.getCoreLibraryDesugaring().getEnabled().get()) {
            project.getLogger().info("Core library desugaring is enabled in: " + project.getPath());

            android.compileOptions(compileOptions -> {
                compileOptions.setCoreLibraryDesugaringEnabled(dslModel.getCoreLibraryDesugaring().getEnabled().get());
                return null;
            });

            project.getDependencies().addProvider("coreLibraryDesugaring", dslModel.getCoreLibraryDesugaring().getLibVersion().map(version -> "com.android.tools:desugar_jdk_libs:" + version));
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void configureTesting(Project project, AndroidSoftware dslModel, CommonExtension<?, ?, ?, ?, ?, ?> android) {
        Testing testing = dslModel.getTesting();
        AndroidTestDependencies testDependencies = testing.getDependencies();

        TestOptions testOptions = testing.getTestOptions();
        ifPresent(testOptions.getTestInstrumentationRunner(), android.getDefaultConfig()::setTestInstrumentationRunner);
        testOptions.getManagedDevices().forEach(device -> {
            ManagedVirtualDevice managedVirtualDevice = android.getTestOptions().getManagedDevices().getDevices().create(device.getName(), ManagedVirtualDevice.class);
            ifPresent(device.getDevice(), managedVirtualDevice::setDevice);
            ifPresent(device.getApiLevel(), managedVirtualDevice::setApiLevel);
            ifPresent(device.getSystemImageSource(), managedVirtualDevice::setSystemImageSource);
        });

        UnitTestOptions unitTestOptions = android.getTestOptions().getUnitTests();
        unitTestOptions.setIncludeAndroidResources(testOptions.getIncludeAndroidResources().get());
        unitTestOptions.setReturnDefaultValues(testOptions.getReturnDefaultValues().get());

        ConfigurationContainer configurations = project.getConfigurations();
        configurations.getByName("testImplementation").fromDependencyCollector(testDependencies.getImplementation());
        configurations.getByName("testCompileOnly").fromDependencyCollector(testDependencies.getCompileOnly());
        configurations.getByName("testRuntimeOnly").fromDependencyCollector(testDependencies.getRuntimeOnly());
        configurations.getByName("androidTestImplementation").fromDependencyCollector(testDependencies.getAndroidImplementation());

        project.getTasks().withType(Test.class).configureEach(test -> test.getFailOnNoDiscoveredTests().set(testing.getFailOnNoDiscoveredTests()));

        configureRoborazzi(project, dslModel);
    }

    public static void configureRoborazzi(Project project, AndroidSoftware dslModel) {
        if (dslModel.getTesting().getRoborazzi().getEnabled().get()) {
            project.getPlugins().apply("io.github.takahirom.roborazzi");
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void configureKotlinSerialization(Project project, AndroidSoftware dslModel) {
        if (dslModel.getKotlinSerialization().getEnabled().get()) {
            project.getLogger().info("Kotlin Serialization is enabled in: " + project.getPath());

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
    public static void linkBuildType(Project project, BuildType buildType, AndroidSoftwareBuildType model, CommonExtension<?, ?, ?, ?, ?, ?> android) {
        buildType.setMinifyEnabled(model.getMinify().getEnabled().get());
        linkBuildTypeDependencies(buildType, model.getDependencies(), project.getConfigurations());

        model.getDefaultProguardFiles().get().forEach(proguardFile -> {
            File defaultProguardFile = android.getDefaultProguardFile(proguardFile.getName().get());
            buildType.proguardFile(defaultProguardFile);
        });
        model.getProguardFiles().get().forEach(proguardFile -> {
            buildType.proguardFile(proguardFile.getName().get());
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void linkBuildTypeDependencies(BuildType buildType, AndroidSoftwareDependencies dependencies, ConfigurationContainer configurations) {
        String name = buildType.getName();
        configurations.getByName(name + "Implementation").fromDependencyCollector(dependencies.getImplementation());
        configurations.getByName(name + "CompileOnly").fromDependencyCollector(dependencies.getCompileOnly());
        configurations.getByName(name + "RuntimeOnly").fromDependencyCollector(dependencies.getRuntimeOnly());
    }
}
