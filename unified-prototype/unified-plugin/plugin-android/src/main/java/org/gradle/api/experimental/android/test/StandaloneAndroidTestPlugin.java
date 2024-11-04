package org.gradle.api.experimental.android.test;

import com.android.build.api.dsl.ManagedVirtualDevice;
import com.android.build.api.dsl.TestExtension;
import com.android.build.api.dsl.UnitTestOptions;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.android.extensions.testing.AndroidTestDependencies;
import org.gradle.api.experimental.android.extensions.testing.TestOptions;
import org.gradle.api.experimental.android.nia.NiaSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;

import static org.gradle.api.experimental.android.AbstractAndroidSoftwarePlugin.DEFAULT_MIN_ANDROID_SDK;
import static org.gradle.api.experimental.android.AndroidSupport.ifPresent;

/**
 * TODO: This class is full of copy-paste.  See the note on {@link AndroidTest}.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class StandaloneAndroidTestPlugin implements Plugin<Project> {
    public static final String ANDROID_TEST = "androidTest";

    @SoftwareType(name = ANDROID_TEST, modelPublicType = AndroidTest.class)
    protected abstract AndroidTest getAndroidTest();

    @Override
    public void apply(Project project) {
        AndroidTest dslModel = getAndroidTest();

        // Setup Android software conventions
        dslModel.getMinSdk().convention(DEFAULT_MIN_ANDROID_SDK);
        dslModel.getBuildConfig().convention(false);

        // Register an afterEvaluate listener before we apply the Android plugin to ensure we can
        // run actions before Android does.
        project.afterEvaluate(p -> linkDslModelToPlugin(p, dslModel));

        // Apply the official Android plugin and support for Kotlin
        project.getPlugins().apply("com.android.test");
        project.getPlugins().apply("org.jetbrains.kotlin.android");

        // Setup other feature extension conventions
        dslModel.getBaselineProfile().getEnabled().convention(false);
        dslModel.getBaselineProfile().getUseConnectedDevices().convention(true);

        // Setup Test Options conventions
        dslModel.getTestOptions().getIncludeAndroidResources().convention(false);
        dslModel.getTestOptions().getReturnDefaultValues().convention(false);
    }

    /**
     * Performs linking actions that must occur within an afterEvaluate block.
     */
    private void linkDslModelToPlugin(Project project, AndroidTest dslModel) {
        TestExtension android = project.getExtensions().getByType(TestExtension.class);

        ifPresent(dslModel.getNamespace(), android::setNamespace);
        ifPresent(dslModel.getCompileSdk(), android::setCompileSdk);
        android.defaultConfig(defaultConfig -> {
            ifPresent(dslModel.getMinSdk(), defaultConfig::setMinSdk);
            return null;
        });

        dslModel.getExperimentalProperties().forEach(property -> {
            android.getExperimentalProperties().put(property.getName(), property.getValue().get());
        });

        ifPresent(dslModel.getTargetProjectPath(), android::setTargetProjectPath);

        android.compileOptions(compileOptions -> {
            // Up to Java 11 APIs are available through desugaring
            // https://developer.android.com/studio/write/java11-minimal-support-table
            compileOptions.setSourceCompatibility(JavaVersion.toVersion(dslModel.getJdkVersion().get()));
            compileOptions.setTargetCompatibility(JavaVersion.toVersion(dslModel.getJdkVersion().get()));
            return null;
        });

        TestOptions testOptions = dslModel.getTestOptions();
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

        AndroidTestDependencies testDependencies = dslModel.getDependencies();
        ConfigurationContainer configurations = project.getConfigurations();
        configurations.getByName("implementation").fromDependencyCollector(testDependencies.getImplementation());
        configurations.getByName("compileOnly").fromDependencyCollector(testDependencies.getCompileOnly());
        configurations.getByName("runtimeOnly").fromDependencyCollector(testDependencies.getRuntimeOnly());
        // AndroidTestImplementation not usable here?

        // TODO:DG All this configuration should be moved to the NiA project
        if (NiaSupport.isNiaProject(project)) {
            NiaSupport.configureNiaTest(project, dslModel);
        }
        ifPresent(dslModel.getBuildConfig(), android.getBuildFeatures()::setBuildConfig);
    }
}
