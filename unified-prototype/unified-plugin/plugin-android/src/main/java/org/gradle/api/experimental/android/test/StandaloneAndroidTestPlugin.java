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
import org.gradle.api.experimental.android.test.internal.DefaultAndroidTestBuildModel;
import org.gradle.api.plugins.PluginManager;
import org.gradle.features.annotations.BindsProjectType;
import org.gradle.features.binding.ProjectFeatureApplicationContext;
import org.gradle.features.binding.ProjectTypeApplyAction;
import org.gradle.features.binding.ProjectTypeBinding;
import org.gradle.features.binding.ProjectTypeBindingBuilder;

import javax.inject.Inject;

import static org.gradle.api.experimental.android.AndroidSupport.ifPresent;

/**
 * TODO: This class is full of copy-paste.  See the note on {@link AndroidTest}.
 */
@SuppressWarnings("UnstableApiUsage")
@BindsProjectType(StandaloneAndroidTestPlugin.Binding.class)
public abstract class StandaloneAndroidTestPlugin implements Plugin<Project> {
    public static final String ANDROID_TEST = "androidTest";

    static class Binding implements ProjectTypeBinding {
        @Override
        public void bind(ProjectTypeBindingBuilder builder) {
            builder.bindProjectType(ANDROID_TEST, AndroidTest.class, ApplyAction.class)
            .withUnsafeDefinition()
            .withUnsafeApplyAction()
            .withBuildModelImplementationType(DefaultAndroidTestBuildModel.class);
        }

        @SuppressWarnings("UnstableApiUsage")
        static abstract class ApplyAction implements ProjectTypeApplyAction<AndroidTest, AndroidTestBuildModel> {
            @Inject
            public ApplyAction() {
            }

            @Inject
            protected abstract PluginManager getPluginManager();

            @Inject
            protected abstract Project getProject();

            @Override
            public void apply(ProjectFeatureApplicationContext context, AndroidTest definition, AndroidTestBuildModel buildModel) {
                // Register an afterEvaluate listener before we apply the Android plugin to ensure we can
                // run actions before Android does.
                getProject().afterEvaluate(p -> linkDefinitionToPlugin(p, definition, buildModel));

                // Apply the official Android plugin and support for Kotlin
                getPluginManager().apply("com.android.test");
                getPluginManager().apply("org.jetbrains.kotlin.android");

                ((DefaultAndroidTestBuildModel) buildModel).setTestExtension(getProject().getExtensions().getByType(TestExtension.class));
            }

            /**
             * Performs linking actions that must occur within an afterEvaluate block.
             */
            private void linkDefinitionToPlugin(Project project, AndroidTest definition, AndroidTestBuildModel buildModel) {
                TestExtension android = buildModel.getTestExtension();

                ifPresent(definition.getNamespace(), android::setNamespace);
                ifPresent(definition.getCompileSdk(), android::setCompileSdk);
                android.defaultConfig(defaultConfig -> {
                    ifPresent(definition.getMinSdk(), defaultConfig::setMinSdk);
                    return null;
                });

                definition.getExperimentalProperties().forEach(property -> {
                    android.getExperimentalProperties().put(property.getName(), property.getValue().get());
                });

                ifPresent(definition.getTargetProjectPath(), android::setTargetProjectPath);

                android.compileOptions(compileOptions -> {
                    // Up to Java 11 APIs are available through desugaring
                    // https://developer.android.com/studio/write/java11-minimal-support-table
                    compileOptions.setSourceCompatibility(JavaVersion.toVersion(definition.getJdkVersion().get()));
                    compileOptions.setTargetCompatibility(JavaVersion.toVersion(definition.getJdkVersion().get()));
                    return null;
                });

                TestOptions testOptions = definition.getTestOptions();
                ifPresent(testOptions.getTestInstrumentationRunner(), android.getDefaultConfig()::setTestInstrumentationRunner);
                testOptions.getManagedDevices().forEach(device -> {
                    ManagedVirtualDevice managedVirtualDevice = android.getTestOptions().getManagedDevices().getAllDevices().create(device.getName(), ManagedVirtualDevice.class);
                    ifPresent(device.getDevice(), managedVirtualDevice::setDevice);
                    ifPresent(device.getApiLevel(), managedVirtualDevice::setApiLevel);
                    ifPresent(device.getSystemImageSource(), managedVirtualDevice::setSystemImageSource);
                });

                UnitTestOptions unitTestOptions = android.getTestOptions().getUnitTests();
                unitTestOptions.setIncludeAndroidResources(testOptions.getIncludeAndroidResources().get());
                unitTestOptions.setReturnDefaultValues(testOptions.getReturnDefaultValues().get());

                AndroidTestDependencies testDependencies = definition.getDependencies();
                ConfigurationContainer configurations = project.getConfigurations();
                configurations.getByName("implementation").fromDependencyCollector(testDependencies.getImplementation());
                configurations.getByName("compileOnly").fromDependencyCollector(testDependencies.getCompileOnly());
                configurations.getByName("runtimeOnly").fromDependencyCollector(testDependencies.getRuntimeOnly());
                // AndroidTestImplementation not usable here?

                // TODO:DG All this configuration should be moved to the NiA project
                if (NiaSupport.isNiaProject(project)) {
                    NiaSupport.configureNiaTest(project, definition);
                }
                ifPresent(definition.getBuildConfig(), android.getBuildFeatures()::setBuildConfig);
            }
        }
    }

    @Override
    public void apply(Project project) { }
}
