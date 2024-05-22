package org.gradle.api.experimental.android.nia;

import com.android.build.api.artifact.SingleArtifact;
import com.android.build.api.dsl.*;
import com.android.build.api.variant.*;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.*;
import org.gradle.api.experimental.android.AndroidSoftware;
import org.gradle.api.experimental.android.extensions.Jacoco;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.testing.Test;
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension;
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension;
import org.gradle.testing.jacoco.tasks.JacocoReport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

// TODO: This class should be moved to the NiA project
/**
 * This is a utility class that configures an Android project with conventions
 * for the Now in Android project.
 * <p>
 * This class is not meant to be used by other projects.
 */
public final class NiaSupport {
    public static final String NIA_PROJECT_NAME = "nowinandroid";

    private NiaSupport() { /* Not instantiable */ }

    public static boolean isNiaProject(Project project) {
        return Objects.equals(project.getRootProject().getName().replaceAll("\\\\-", ""), NiaSupport.NIA_PROJECT_NAME);
    }

    public static void configureNiaLibrary(Project project, AndroidSoftware dslModel) {
        LibraryExtension androidLib = project.getExtensions().getByType(LibraryExtension.class);
        LibraryAndroidComponentsExtension androidLibComponents = project.getExtensions().getByType(LibraryAndroidComponentsExtension.class);

        configureNia(project, dslModel, androidLib, androidLibComponents);
        disableUnnecessaryAndroidTests(project, androidLibComponents);
    }

    public static void configureNiaApplication(Project project, AndroidSoftware dslModel) {
        ApplicationExtension androidApp = project.getExtensions().getByType(ApplicationExtension.class);
        ApplicationAndroidComponentsExtension androidAppComponents = project.getExtensions().getByType(ApplicationAndroidComponentsExtension.class);

        configureNia(project, dslModel, androidApp, androidAppComponents);
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void configureNia(Project project, AndroidSoftware dslModel, CommonExtension<?, ?, ?, ?, ?, ?> android, AndroidComponentsExtension<?, ?, ?> androidComponents) {
        dslModel.getDependencies().getImplementation().add("androidx.tracing:tracing-ktx:1.3.0-alpha02");
        dslModel.getTesting().getDependencies().getImplementation().add("org.jetbrains.kotlin:kotlin-test");

        android.setResourcePrefix(buildResourcePrefix(project));
        configureFlavors(android, (flavor, niaFlavor) -> {});
        configureKotlin(project);

        configureGradleManagedDevices(android);
        configureLint(android);
        configurePrintApksTask(project, androidComponents);

        if (dslModel.getTesting().getJacoco().getEnabled().get()) {
            configureJacoco(dslModel.getTesting().getJacoco(), project, android);
        }

        if (dslModel.getFeature().getEnabled().get()) {
            configureFeature(project, android);
        }
    }


    @SuppressWarnings("deprecation")
    private static void disableUnnecessaryAndroidTests(Project project, LibraryAndroidComponentsExtension androidLibComponents) {
        androidLibComponents.beforeVariants(androidLibComponents.selector().all(), it -> {
            it.setEnableAndroidTest(it.getEnableAndroidTest() && project.getLayout().getProjectDirectory().file("src/androidTest").getAsFile().exists());
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void configureFeature(Project project, CommonExtension<?, ?, ?, ?, ?, ?> android) {
        android.getDefaultConfig().setTestInstrumentationRunner("com.google.samples.apps.nowinandroid.core.testing.NiaTestRunner");
        android.getTestOptions().setAnimationsDisabled(true);

        project.getDependencies().add("implementation", project.project(":core:ui"));
        project.getDependencies().add("implementation", project.project(":core:designsystem"));

        project.getDependencies().add("implementation", "androidx.hilt:hilt-navigation-compose:1.2.0");
        project.getDependencies().add("implementation", "androidx.lifecycle:lifecycle-runtime-compose:2.7.0");
        project.getDependencies().add("implementation", "androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0");
        project.getDependencies().add("implementation", "androidx.tracing:tracing-ktx:1.3.0-alpha02");

        project.getDependencies().add("androidTestImplementation", "androidx.lifecycle:lifecycle-runtime-testing:2.7.0");
    }

    private static void configureLint(CommonExtension<?, ?, ?, ?, ?, ?> android) {
        android.getLint().setXmlReport(true);
        android.getLint().setCheckDependencies(true);
    }

    private static void configureFlavors(
            CommonExtension<?, ?, ?, ?, ?, ?> android,
            BiConsumer<ProductFlavor, NiaFlavor> flavorConfigurationBlock) {
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
            // Set JVM target to 11
            kotlinOptions.setJvmTarget(JavaVersion.VERSION_11.toString());

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
                "**/Manifest*.*"
        );
    }

    @SuppressWarnings("deprecation")
    private static void configureJacoco(Jacoco jacocoExtension, Project project, CommonExtension<?, ?, ?, ?, ?, ?> android) {
        project.getPlugins().apply("jacoco");

        android.getBuildTypes().configureEach(buildType -> {
            buildType.setEnableAndroidTestCoverage(true);
            buildType.setEnableUnitTestCoverage(true);
        });

        JacocoPluginExtension jacocoPluginExtension = project.getExtensions().getByType(JacocoPluginExtension.class);
        jacocoPluginExtension.setToolVersion(jacocoExtension.getVersion().get());

        LibraryAndroidComponentsExtension androidLibComponents = project.getExtensions().getByType(LibraryAndroidComponentsExtension.class);
        androidLibComponents.onVariants(androidLibComponents.selector().all(), variant -> {
            final String testTaskName = "test" + StringUtils.capitalize(variant.getName()) + "UnitTest";
            final File buildDir = project.getLayout().getBuildDirectory().get().getAsFile();
            project.getTasks().register("jacoco" + StringUtils.capitalize(testTaskName) + "Report", JacocoReport.class, task -> {
                task.dependsOn(testTaskName);

                task.reports(report -> {
                    report.getXml().getRequired().set(true);
                    report.getXml().getRequired().set(true);
                });

                task.getClassDirectories().setFrom(
                        project.fileTree(project.getBuildDir() + "/tmp/kotlin-classes/" + variant.getName(), tree -> tree.exclude(coverageExclusions()))
                );

                task.getSourceDirectories().setFrom(
                        project.files(project.getProjectDir() + "/src/main/java", project.getProjectDir() + "/src/main/kotlin"
                        ));

                task.getExecutionData().setFrom(
                        project.files(buildDir + "/jacoco/" + testTaskName + ".exec")
                );
            });
        });

        project.getTasks().withType(Test.class, test -> {
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
