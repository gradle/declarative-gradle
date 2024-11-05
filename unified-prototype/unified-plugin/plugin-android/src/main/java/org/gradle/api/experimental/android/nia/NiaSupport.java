package org.gradle.api.experimental.android.nia;

import androidx.baselineprofile.gradle.consumer.BaselineProfileConsumerExtension;
import androidx.baselineprofile.gradle.producer.BaselineProfileProducerExtension;
import com.android.SdkConstants;
import com.android.build.api.artifact.ScopedArtifact;
import com.android.build.api.artifact.SingleArtifact;
import com.android.build.api.dsl.ApplicationExtension;
import com.android.build.api.dsl.ApplicationProductFlavor;
import com.android.build.api.dsl.CommonExtension;
import com.android.build.api.dsl.Device;
import com.android.build.api.dsl.DeviceGroup;
import com.android.build.api.dsl.LibraryExtension;
import com.android.build.api.dsl.ManagedVirtualDevice;
import com.android.build.api.dsl.ProductFlavor;
import com.android.build.api.dsl.TestOptions;
import com.android.build.api.dsl.VariantDimension;
import com.android.build.api.variant.AndroidComponentsExtension;
import com.android.build.api.variant.ApplicationAndroidComponentsExtension;
import com.android.build.api.variant.BuiltArtifactsLoader;
import com.android.build.api.variant.HasAndroidTest;
import com.android.build.api.variant.LibraryAndroidComponentsExtension;
import com.android.build.api.variant.ScopedArtifacts;
import com.android.build.api.variant.TestAndroidComponentsExtension;
import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.TestExtension;
import com.dropbox.gradle.plugins.dependencyguard.DependencyGuardPluginExtension;
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.gradle.api.*;
import org.gradle.api.experimental.android.AndroidSoftware;
import org.gradle.api.experimental.android.application.AndroidApplication;
import org.gradle.api.experimental.android.extensions.BaselineProfile;
import org.gradle.api.experimental.android.library.AndroidLibrary;
import org.gradle.api.experimental.android.test.AndroidTest;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.Directory;
import org.gradle.api.file.RegularFile;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.testing.Test;
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension;
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension;
import org.gradle.testing.jacoco.tasks.JacocoReport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.gradle.api.experimental.android.AndroidSupport.ifPresent;

// TODO:DG This class should be moved to the NiA project
/**
 * This is a utility class that configures an Android project with conventions
 * for the Now in Android project.
 * <p>
 * This class is not meant to be used by other projects.
 */
public final class NiaSupport {
    private static final Integer DEFAULT_TARGET_SDK = 34;
    public static final String NIA_PROJECT_NAME = "nowinandroid";

    private NiaSupport() { /* Not instantiable */ }

    public static boolean isNiaProject(Project project) {
        return Objects.equals(project.getRootProject().getName().replace("-", ""), NiaSupport.NIA_PROJECT_NAME);
    }

    public static void configureNiaTest(Project project, AndroidTest dslModel) {
        TestExtension androidTest = project.getExtensions().getByType(TestExtension.class);
        TestAndroidComponentsExtension androidTestComponents = project.getExtensions().getByType(TestAndroidComponentsExtension.class);

        androidTest.getDefaultConfig().setTargetSdkPreview(dslModel.getTargetSdk().getOrElse(DEFAULT_TARGET_SDK).toString());

        // Use the same flavor dimensions as the application to allow generating Baseline Profiles on prod,
        // which is more close to what will be shipped to users (no fake data), but has ability to run the
        // benchmarks on demo, so we benchmark on stable data.
        NiaSupport.configureFlavors(androidTest, (vd, flavor) -> {
            vd.buildConfigField(
                "String",
                "APP_FLAVOR_SUFFIX",
                "\"" + (flavor.applicationIdSuffix == null ? "" : flavor.applicationIdSuffix) + "\""
            );
        });

        configureKotlin(project);

        configureGradleManagedDevices(androidTest);
        configurePrintApksTask(project, androidTestComponents);

        if (project.getExtensions().findByName("baselineProfile") != null) {
            BaselineProfileProducerExtension baselineProfileProducerExtension = project.getExtensions().getByType(BaselineProfileProducerExtension.class);
            BaselineProfileConsumerExtension baselineProfileConsumerExtension = project.getExtensions().getByType(BaselineProfileConsumerExtension.class);
            configureBaselineProfile(project, dslModel.getBaselineProfile(), baselineProfileProducerExtension, baselineProfileConsumerExtension);
        }
    }

    public static void configureNiaLibrary(Project project, AndroidLibrary dslModel) {
        LibraryExtension androidLib = project.getExtensions().getByType(LibraryExtension.class);
        LibraryAndroidComponentsExtension androidLibComponents = project.getExtensions().getByType(LibraryAndroidComponentsExtension.class);

        //noinspection deprecation
        androidLib.getDefaultConfig().setTargetSdkPreview(dslModel.getTargetSdk().getOrElse(DEFAULT_TARGET_SDK).toString());

        configureFlavors(androidLib);

        androidLib.setResourcePrefix(buildResourcePrefix(project));

        configureNia(project, dslModel, androidLib, androidLibComponents);
        disableUnnecessaryAndroidTests(project, androidLibComponents);
    }

    public static void configureNiaApplication(Project project, AndroidApplication dslModel) {
        ApplicationExtension androidApp = project.getExtensions().getByType(ApplicationExtension.class);
        ApplicationAndroidComponentsExtension androidAppComponents = project.getExtensions().getByType(ApplicationAndroidComponentsExtension.class);

        androidApp.getDefaultConfig().setTargetSdkPreview(dslModel.getTargetSdk().getOrElse(DEFAULT_TARGET_SDK).toString());

        if (dslModel.getFlavors().getEnabled().get()) {
            configureFlavors(androidApp);
        }
        if (dslModel.getMissingDimensionStrategy().getName().isPresent()) {
            androidApp.getDefaultConfig().missingDimensionStrategy(dslModel.getMissingDimensionStrategy().getName().get(), dslModel.getMissingDimensionStrategy().getValue().get());
        }

        configureNia(project, dslModel, androidApp, androidAppComponents);

        @SuppressWarnings("UnstableApiUsage")
        TestOptions testOptions = androidApp.getTestOptions();
        testOptions.setAnimationsDisabled(true);

        ifPresent(dslModel.getBuildTypes().getDebug().getApplicationIdSuffix(), androidApp.getBuildTypes().getByName("debug")::setApplicationIdSuffix);
        ifPresent(dslModel.getBuildTypes().getRelease().getApplicationIdSuffix(), androidApp.getBuildTypes().getByName("release")::setApplicationIdSuffix);

        configureBadgingTasks(project, androidAppComponents);

        configureDependencyGuard(project, dslModel);
        configureFirebase(project, dslModel, androidApp);
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void configureNia(Project project, AndroidSoftware dslModel, CommonExtension<?, ?, ?, ?, ?, ?> android, AndroidComponentsExtension<?, ?, ?> androidComponents) {
        dslModel.getDependencies().getImplementation().add("androidx.tracing:tracing-ktx:1.3.0-alpha02");
        dslModel.getTesting().getDependencies().getImplementation().add("org.jetbrains.kotlin:kotlin-test");

        configureKotlin(project);

        configureGradleManagedDevices(android);
        configurePrintApksTask(project, androidComponents);

        configureJacoco(project, dslModel, android);

        configureFeature(project, dslModel, android);
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void configureFirebase(Project project, AndroidApplication dslModel, ApplicationExtension androidApp) {
        if (dslModel.getFirebase().getEnabled().get()) {
            project.getPlugins().apply("com.google.gms.google-services");
            project.getPlugins().apply("com.google.firebase.firebase-perf");
            project.getPlugins().apply("com.google.firebase.crashlytics");

            dslModel.getDependencies().getImplementation().add(project.getDependencies().platform("com.google.firebase:firebase-bom:" + dslModel.getFirebase().getVersion().get()));
            dslModel.getDependencies().getImplementation().add("com.google.firebase:firebase-analytics-ktx");
            dslModel.getDependencies().getImplementation().add("com.google.firebase:firebase-perf-ktx");
            dslModel.getDependencies().getImplementation().add("com.google.firebase:firebase-crashlytics-ktx");

            androidApp.getBuildTypes().configureEach(buildType -> {
                CrashlyticsExtension crashlyticsExtension = buildType.getExtensions().getByType(CrashlyticsExtension.class);
                crashlyticsExtension.setMappingFileUploadEnabled(dslModel.getFirebase().getMappingFileUploadEnabled().get());
            });
        }
    }

    private static void configureBadgingTasks(Project project, ApplicationAndroidComponentsExtension androidAppComponents) {
        BaseExtension baseExtension = project.getExtensions().getByType(BaseExtension.class);

        androidAppComponents.onVariants(androidAppComponents.selector().all(), variant -> {
            // Registers a new task to verify the app bundle.
            String capitalizedVariantName = WordUtils.capitalize(variant.getName());
            String generateBadgingTaskName = "generate" + capitalizedVariantName + "Badging";
            Provider<GenerateBadgingTask> generateBadging = project.getTasks().register(generateBadgingTaskName, GenerateBadgingTask.class, task -> {
                @SuppressWarnings("UnstableApiUsage") SingleArtifact.APK_FROM_BUNDLE apkFromBundle = SingleArtifact.APK_FROM_BUNDLE.INSTANCE;
                task.getApk().set(variant.getArtifacts().get(apkFromBundle));

                task.getAapt2Executable().set(
                        new File(baseExtension.getSdkDirectory(),
                                SdkConstants.FD_BUILD_TOOLS + "/" + baseExtension.getBuildToolsVersion() + "/" + SdkConstants.FN_AAPT2));

                task.getBadging().set(project.getLayout().getBuildDirectory().file("outputs/apk_from_bundle/" + variant.getName() + "/" + variant.getName() + "-badging.txt"));
            });

            String updateBadgingTaskName = "update" + capitalizedVariantName + "Badging";
            project.getTasks().register(updateBadgingTaskName, Copy.class, task -> {
                task.from(generateBadging.get().getBadging());
                task.into(project.getLayout().getProjectDirectory());
            });

            String checkBadgingTaskName = "check" + capitalizedVariantName + "Badging";
            project.getTasks().register(checkBadgingTaskName, CheckBadgingTask.class, task -> {
                task.getGoldenBadging().set(project.getLayout().getProjectDirectory().file(variant.getName() + "-badging.txt"));
                task.getGeneratedBadging().set(generateBadging.get().getBadging());
                task.getUpdateBadgingTaskName().set(updateBadgingTaskName);
                task.getOutput().set(project.getLayout().getBuildDirectory().dir("intermediates/" + checkBadgingTaskName));
            });
        });
    }

    private static void disableUnnecessaryAndroidTests(Project project, LibraryAndroidComponentsExtension androidLibComponents) {
        androidLibComponents.beforeVariants(androidLibComponents.selector().all(), it -> {
            it.setEnableAndroidTest(it.getEnableAndroidTest() && project.getLayout().getProjectDirectory().file("src/androidTest").getAsFile().exists());
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void configureFeature(Project project, AndroidSoftware dslModel, CommonExtension<?, ?, ?, ?, ?, ?> android) {
        if (dslModel.getFeature().getEnabled().get()) {
            project.getLogger().info(project.getPath() + " is a conventional Now In Android Feature project");

            android.getTestOptions().setAnimationsDisabled(true);

            project.getDependencies().add("implementation", project.project(":core:ui"));
            project.getDependencies().add("implementation", project.project(":core:designsystem"));

            project.getDependencies().add("implementation", "androidx.hilt:hilt-navigation-compose:1.2.0");
            project.getDependencies().add("implementation", "androidx.lifecycle:lifecycle-runtime-compose:2.8.6");
            project.getDependencies().add("implementation", "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6");
            project.getDependencies().add("implementation", "androidx.tracing:tracing-ktx:1.3.0-alpha02");

            project.getDependencies().add("androidTestImplementation", "androidx.lifecycle:lifecycle-runtime-testing:2.8.6");
        }
    }

    /**
     * All NiA Android libraries get flavors, but only NiA Applications that specifically ask
     * for them will also get flavors.
     *
     * @param android the Android extension to configure
     */
    private static void configureFlavors(CommonExtension<?, ?, ?, ?, ?, ?> android) {
        configureFlavors(android, (dimension, flavor) -> {});
    }

    /**
     * All NiA Android libraries get flavors, but only NiA Applications that specifically ask
     * for them will also get flavors.
     *
     * @param android the Android extension to configure
     * @param flavorConfigurationBlock additional configuration to perform on each flavor
     */
    public static void configureFlavors(CommonExtension<?, ?, ?, ?, ?, ?> android,
                                        BiConsumer<VariantDimension, NiaFlavor> flavorConfigurationBlock) {
        android.getFlavorDimensions().add(FlavorDimension.contentType.name());

        Arrays.stream(NiaFlavor.values()).forEach(it -> android.getProductFlavors().create(it.name(), flavor -> {
            setDimensionReflectively(flavor, it.dimension.name());
            flavorConfigurationBlock.accept(flavor, it);
            if (android instanceof ApplicationExtension && flavor instanceof ApplicationProductFlavor) {
                if (it.applicationIdSuffix != null) {
                    ((ApplicationProductFlavor) flavor).setApplicationIdSuffix(it.applicationIdSuffix);
                }
            }
        }));
    }

    /**
     * This method uses reflection to call setDimension on the ProductFlavor.
     * <p>
     * This is necessary because otherwise calling a method with a {@code String?} argument
     * from Java results in the following compile error:
     * <pre>
     * flavor.setDimension(name);
     *                       ^
     *   both method setDimension(String) in ProductFlavor and method setDimension(String) in ProductFlavor match
     * </pre>
     * @param flavor the flavor to set the dimension on
     * @param name the name of the dimension
     */
    private static void setDimensionReflectively(ProductFlavor flavor, String name) {
        Method setDimension;
        try {
            setDimension = ProductFlavor.class.getMethod("setDimension", String.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find setDimension on flavor: " + flavor, e);
        }
        try {
            setDimension.invoke(flavor, name);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call setDimension on flavor: " + flavor + " with: " + name, e);
        }
    }

    /**
     * Builds a resource prefix based on the project path.
     * <p>
     * The resource prefix is derived from the module name,
     * so resources inside ":core:module1" must be prefixed with "core_module1_".
     *
     * @param project the project to derive the resource prefix from
     * @return the computed resource prefix
     */
    private static @NotNull String buildResourcePrefix(Project project) {
        String[] parts = project.getPath().split("\\W+");
        String result = Arrays.stream(parts)
                .skip(1)  // Skipping the first element
                .distinct()  // Why? This was in the original code though
                .collect(Collectors.joining("_"))
                .toLowerCase();
        result += "_";
        return result;
    }

    private static void configureKotlin(Project project) {
        project.getTasks().withType(KotlinCompile.class, task -> {
            KotlinJvmOptions kotlinOptions = task.getKotlinOptions();
            // Set JVM target to 17
            kotlinOptions.setJvmTarget(JavaVersion.VERSION_17.toString());

            // Treat all Kotlin warnings as errors (disabled by default)
            // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
            boolean warningsAsErrors = false;
            if (project.hasProperty("warningsAsErrors")) {
                warningsAsErrors = Boolean.parseBoolean(project.getProperties().get("warningsAsErrors").toString());
            }
            kotlinOptions.setAllWarningsAsErrors(warningsAsErrors);

            List<String> freeCompilerArgs = new ArrayList<>(kotlinOptions.getFreeCompilerArgs());
            freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi");
            kotlinOptions.setFreeCompilerArgs(freeCompilerArgs);
        });
    }

    /**
     * Configure project for Gradle managed devices
     */
    @SuppressWarnings("UnstableApiUsage")
    private static void configureGradleManagedDevices(CommonExtension<?, ?, ?, ?, ?, ?> android) {
        DeviceConfig pixel4 = new DeviceConfig("Pixel 4", 30, "aosp-atd");
        DeviceConfig pixel6 = new DeviceConfig("Pixel 6", 31, "aosp");
        DeviceConfig pixelC = new DeviceConfig("Pixel C", 30, "aosp-atd");

        List<DeviceConfig> allDevices = Arrays.asList(pixel4, pixel6, pixelC);
        List<DeviceConfig> ciDevices = Arrays.asList(pixel4, pixelC);

        TestOptions testOptions = android.getTestOptions();

        ExtensiblePolymorphicDomainObjectContainer<Device> devices = testOptions.getManagedDevices().getDevices();
        allDevices.forEach(deviceConfig -> {
            ManagedVirtualDevice newDevice = devices.maybeCreate(deviceConfig.getTaskName(), ManagedVirtualDevice.class);
            newDevice.setDevice(deviceConfig.getDevice());
            newDevice.setApiLevel(deviceConfig.getApiLevel());
            newDevice.setSystemImageSource(deviceConfig.getSystemImageSource());
        });

        NamedDomainObjectContainer<DeviceGroup> groups = testOptions.getManagedDevices().getGroups();
        DeviceGroup newGroup = groups.maybeCreate("ci");
        ciDevices.forEach(deviceConfig -> newGroup.getTargetDevices().add(devices.getByName(deviceConfig.getTaskName())));
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void configurePrintApksTask(Project project, AndroidComponentsExtension<?, ?, ?> androidComponents) {
        androidComponents.onVariants(androidComponents.selector().all(), variant -> {
            if (variant instanceof HasAndroidTest hasAndroidTestVariant) {
                BuiltArtifactsLoader loader = variant.getArtifacts().getBuiltArtifactsLoader();
                Provider<Directory> artifact = hasAndroidTestVariant.getAndroidTest() != null ? hasAndroidTestVariant.getAndroidTest().getArtifacts().get(SingleArtifact.APK.INSTANCE) : null;
                Provider<? extends Collection<Directory>> javaSources = hasAndroidTestVariant.getAndroidTest() != null ? Objects.requireNonNull(hasAndroidTestVariant.getAndroidTest().getSources().getJava()).getAll() : null;
                Provider<? extends Collection<Directory>> kotlinSources = hasAndroidTestVariant.getAndroidTest() != null ? Objects.requireNonNull(hasAndroidTestVariant.getAndroidTest().getSources().getKotlin()).getAll() : null;

                Provider<? extends Collection<Directory>> testSources;
                if (javaSources != null && kotlinSources != null) {
                    testSources = javaSources.zip(kotlinSources, (javaDirs, kotlinDirs) -> {
                        List<Directory> dirs = new ArrayList<>(javaDirs);
                        dirs.addAll(kotlinDirs);
                        return dirs;
                    });
                } else {
                    if (javaSources != null) {
                        testSources = javaSources;
                    } else {
                        testSources = kotlinSources;
                    }
                }

                if (artifact != null && testSources != null) {
                    project.getTasks().register(
                            variant.getName() + "PrintTestApk",
                            PrintApkLocationTask.class, task -> {
                                task.getApkFolder().set(artifact);
                                task.getBuiltArtifactsLoader().set(loader);
                                task.getVariantName().set(variant.getName());
                                task.getSources().set(testSources);
                            }
                    );
                }
            }
        });
    }

    private static List<String> coverageExclusions() {
        return Arrays.asList(
                "**/R.class",
                "**/R$*.class",
                "**/BuildConfig.*",
                "**/Manifest*.*",
                "**/*_Hilt*.class",
                "**/Hilt_*.class"
        );
    }

    private static void configureJacoco(Project project, AndroidSoftware dslModel, CommonExtension<?, ?, ?, ?, ?, ?> android) {
        if (dslModel.getTesting().getJacoco().getEnabled().get()) {
            project.getLogger().info("JaCoCo is enabled in: " + project.getPath());

            project.getPlugins().apply("jacoco");

            android.getBuildTypes().configureEach(buildType -> {
                buildType.setEnableAndroidTestCoverage(true);
                buildType.setEnableUnitTestCoverage(true);
            });

            JacocoPluginExtension jacocoPluginExtension = project.getExtensions().getByType(JacocoPluginExtension.class);
            jacocoPluginExtension.setToolVersion(dslModel.getTesting().getJacoco().getVersion().get());

            AndroidComponentsExtension<?, ?, ?> androidComponentsExtension = project.getExtensions().getByType(AndroidComponentsExtension.class);
            androidComponentsExtension.onVariants(androidComponentsExtension.selector().all(), variant -> {
                final ObjectFactory objectFactory = project.getObjects();
                final Directory buildDir = project.getLayout().getBuildDirectory().get();
                final ListProperty<RegularFile> allJars = objectFactory.listProperty(RegularFile.class);
                final ListProperty<Directory> allDirectories = objectFactory.listProperty(Directory.class);

                TaskProvider<JacocoReport> reportTask = project.getTasks().register("create" + StringUtils.capitalize(variant.getName()) + "CombinedCoverageReport", JacocoReport.class, task -> {
                    task.getClassDirectories().setFrom(
                        allJars,
                        allDirectories.map(dirs -> dirs.stream().map(dir -> objectFactory.fileTree().setDir(dir).exclude(coverageExclusions())))
                    );

                    task.getReports().getXml().getRequired().set(true);
                    task.getReports().getHtml().getRequired().set(true);

                    // TODO: This is missing files in src/debug/, src/prod, src/demo, src/demoDebug...
                    String projectDir = project.getLayout().getProjectDirectory().getAsFile().getPath();
                    task.getSourceDirectories().setFrom(project.files(projectDir + "/src/main/java", projectDir + "/src/main/kotlin"));

                    ConfigurableFileTree unitTestCoverage = project.fileTree(buildDir.file("/outputs/unit_test_code_coverage/" + variant.getName() + "UnitTest"));
                    unitTestCoverage.matching(unitTestCoverage.include("**/*.exec"));
                    ConfigurableFileTree androidTestCoverage = project.fileTree(buildDir.file("/outputs/code_coverage/" + variant.getName() + "AndroidTest"));
                    androidTestCoverage.matching(androidTestCoverage.include("**/*.ec"));

                    task.getExecutionData().setFrom(unitTestCoverage, androidTestCoverage);
                });

                variant.getArtifacts().forScope(ScopedArtifacts.Scope.PROJECT)
                    .use(reportTask)
                    .toGet(ScopedArtifact.CLASSES.INSTANCE, ignore -> allJars, ignore -> allDirectories);
            });

            project.getTasks().withType(Test.class).configureEach(test -> {
                JacocoTaskExtension jacocoTaskExtension = test.getExtensions().getByType(JacocoTaskExtension.class);

                // Required for JaCoCo + Robolectric
                // https://github.com/robolectric/robolectric/issues/2230
                // Consider removing if not we don't add Robolectric
                jacocoTaskExtension.setIncludeNoLocationClasses(true);

                // Required for JDK 11 with the above
                // https://github.com/gradle/gradle/issues/5184#issuecomment-391982009
                jacocoTaskExtension.setExcludes(Collections.singletonList("jdk.internal.*"));
            });
        }
    }

    private static void configureDependencyGuard(Project project, AndroidApplication dslModel) {
        if (dslModel.getDependencyGuard().getEnabled().get()) {
            // Slight change of behavior here - NiA just applies this plugin to all applications, which seems unnecessary
            project.getPlugins().apply("com.dropbox.dependency-guard");

            DependencyGuardPluginExtension dependencyGuard = project.getExtensions().getByType(DependencyGuardPluginExtension.class);
            dependencyGuard.configuration(dslModel.getDependencyGuard().getConfigurationName().get());
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void configureBaselineProfile(Project project, BaselineProfile baselineProfile, BaselineProfileProducerExtension baselineProfileProducerExtension, BaselineProfileConsumerExtension baselineProfileConsumerExtension) {
        if (baselineProfile.getEnabled().get()) {
            project.getPlugins().apply("androidx.baselineprofile");

            baselineProfileConsumerExtension.setAutomaticGenerationDuringBuild(baselineProfile.getAutomaticGenerationDuringBuild().get());

            if (baselineProfile.getAdditionalManagedDevice().isPresent()) {
                baselineProfileProducerExtension.getManagedDevices().add(baselineProfile.getAdditionalManagedDevice().get());
            }
            ifPresent(baselineProfile.getUseConnectedDevices(), baselineProfileProducerExtension::setUseConnectedDevices);

            project.getConfigurations().getByName("baselineProfile").fromDependencyCollector(baselineProfile.getDependencies().getProfile());
        }
    }
}
