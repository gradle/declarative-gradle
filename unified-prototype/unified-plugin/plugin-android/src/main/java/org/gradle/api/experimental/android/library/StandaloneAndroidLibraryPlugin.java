package org.gradle.api.experimental.android.library;

import com.android.build.api.attributes.ProductFlavorAttr;
import com.android.build.api.dsl.BuildType;
import com.android.build.api.dsl.LibraryBuildType;
import com.android.build.api.dsl.LibraryExtension;
import org.gradle.api.JavaVersion;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension;

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
        dslModel.getJdkVersion().convention(17);
        dslModel.getCompileSdk().convention(34);
        dslModel.getMinSdk().convention(21); // https://developer.android.com/build/multidex#mdex-gradle
        dslModel.getIncludeKotlinSerialization().convention(false);

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
            compileOptions.setSourceCompatibility(JavaVersion.VERSION_11);
            compileOptions.setTargetCompatibility(JavaVersion.VERSION_11);
            compileOptions.setCoreLibraryDesugaringEnabled(!dslModel.getDependencies().getCoreLibraryDesugaring().getDependencies().get().isEmpty());
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
        setContentTypeAttributes(project);

        if (dslModel.getIncludeKotlinSerialization().get()) {
            project.getPluginManager().apply("kotlinx-serialization");
        }
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

    private static void setContentTypeAttributes(Project project) {
        // These attributes must be set to avoid Ambiguous Variants resolution errors between the
        // demoDebugRuntimeElements and prodDebugRuntimeElements for project dependencies in NiA
        // TODO: They are not set by the NiA build because it doesn't know about the product flavor yet
        project.getConfigurations().configureEach(c -> {
            AttributeContainer attributes = c.getAttributes();
            String lowerConfName = c.getName().toLowerCase();
            if (lowerConfName.contains("debug")) {
                attributes.attribute(ProductFlavorAttr.of("contentType"), project.getObjects().named(ProductFlavorAttr.class, "demo"));
                attributes.attribute(Attribute.of("contentType", String.class), "demo");
            } else if (lowerConfName.contains("release")) {
                attributes.attribute(ProductFlavorAttr.of("contentType"), project.getObjects().named(ProductFlavorAttr.class, "prod"));
                attributes.attribute(Attribute.of("contentType", String.class), "prod");
            }
        });
    }

    private static void linkBuildTypeDependencies(BuildType buildType, AndroidLibraryDependencies dependencies, ConfigurationContainer configurations) {
        String name = buildType.getName();
        configurations.getByName(name + "Implementation").fromDependencyCollector(dependencies.getImplementation());
        configurations.getByName(name + "Api").fromDependencyCollector(dependencies.getApi());
        configurations.getByName(name + "CompileOnly").fromDependencyCollector(dependencies.getCompileOnly());
        configurations.getByName(name + "RuntimeOnly").fromDependencyCollector(dependencies.getRuntimeOnly());
    }
}
