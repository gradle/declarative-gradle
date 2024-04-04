package org.gradle.api.experimental.android.library;

import com.android.build.api.dsl.BuildType;
import com.android.build.api.dsl.LibraryBuildType;
import com.android.build.api.dsl.LibraryExtension;
import org.gradle.api.JavaVersion;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension;

import static org.gradle.api.experimental.android.AndroidDSLSupport.ifPresent;

/**
 * Creates a declarative {@link AndroidLibrary} DSL model, applies the official Android plugin,
 * and links the declarative model to the official plugin.
 */
public abstract class StandaloneAndroidLibraryPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        AndroidLibrary dslModel = project.getExtensions().create("androidLibrary", AndroidLibrary.class);

        // Register an afterEvaluate listener before we apply the Android plugin to ensure we can
        // run actions before Android does.
        project.afterEvaluate(p -> linkDslModelToPlugin(p, dslModel));

        // Apply the official Android plugin.
        project.getPlugins().apply("com.android.library");
        project.getPlugins().apply("org.jetbrains.kotlin.android");

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
    }

    /**
     * Performs linking actions that do not need to occur within an afterEvaluate block.
     */
    public static void linkDslModelToPluginLazy(Project project, AndroidLibrary dslModel) {
        ConfigurationContainer configurations = project.getConfigurations();
        linkCommonDependencies(dslModel.getDependencies(), configurations);
    }

    private static void linkCommonDependencies(LibraryDependencies dependencies, ConfigurationContainer configurations) {
        configurations.getByName("implementation").fromDependencyCollector(dependencies.getImplementation());
        configurations.getByName("api").fromDependencyCollector(dependencies.getApi());
        configurations.getByName("compileOnly").fromDependencyCollector(dependencies.getCompileOnly());
        configurations.getByName("runtimeOnly").fromDependencyCollector(dependencies.getRuntimeOnly());
    }

    /**
     * Links build types from the model to the android extension.
     */
    private static void linkBuildType(LibraryBuildType android, AndroidLibraryBuildType model, ConfigurationContainer configurations) {
        ifPresent(model.getMinifyEnabled(), android::setMinifyEnabled);
        linkBuildTypeDependencies(android, model.getDependencies(), configurations);
    }

    private static void linkBuildTypeDependencies(BuildType buildType, LibraryDependencies dependencies, ConfigurationContainer configurations) {
        String name = buildType.getName();
        configurations.getByName(name + "Implementation").fromDependencyCollector(dependencies.getImplementation());
        configurations.getByName(name + "Api").fromDependencyCollector(dependencies.getApi());
        configurations.getByName(name + "CompileOnly").fromDependencyCollector(dependencies.getCompileOnly());
        configurations.getByName(name + "RuntimeOnly").fromDependencyCollector(dependencies.getRuntimeOnly());
    }
}
