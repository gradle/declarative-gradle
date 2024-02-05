package org.gradle.api.experimental.android;

import com.android.build.api.dsl.LibraryExtension;
import com.android.build.api.variant.AndroidComponentsExtension;
import org.gradle.api.*;
import org.gradle.api.provider.Property;
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a declarative {@link AndroidLibrary} DSL model, applies the official Android plugin,
 * and links the declarative model to the official plugin.
 */
public class StandaloneAndroidLibraryPlugin implements Plugin<Project> {
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
    private void linkDslModelToPlugin(Project project, AndroidLibrary dslModel) {
        LibraryExtension android = project.getExtensions().getByType(LibraryExtension.class);
        KotlinAndroidProjectExtension kotlin = project.getExtensions().getByType(KotlinAndroidProjectExtension.class);

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
    }

    /**
     * Performs linking actions that do not need to occur within an afterEvaluate block.
     */
    private void linkDslModelToPluginLazy(Project project, AndroidLibrary dslModel) {
        AndroidComponentsExtension<?, ?, ?> androidComponents = project.getExtensions().getByType(AndroidComponentsExtension.class);

        // Link common dependencies
        project.getConfigurations().getByName("implementation").getDependencies()
            .addAllLater(dslModel.getDependencies().getImplementation().getDependencies());
        project.getConfigurations().getByName("api").getDependencies()
            .addAllLater(dslModel.getDependencies().getApi().getDependencies());
        project.getConfigurations().getByName("compileOnly").getDependencies()
            .addAllLater(dslModel.getDependencies().getCompileOnly().getDependencies());
        project.getConfigurations().getByName("runtimeOnly").getDependencies()
            .addAllLater(dslModel.getDependencies().getRuntimeOnly().getDependencies());

        // Link target-specific properties
        androidComponents.beforeVariants(androidComponents.selector().all(), variant -> {
            AndroidTarget target = dslModel.getTargets().findByName(variant.getName());
            if (target == null) {
                // The user did not add any target-specific configuration.
                return;
            }

            ifPresent(target.getMinSdk(), variant::setMinSdk);
        });

        // Link target-specific dependencies
        List<String> variantNames = new ArrayList<>();
        androidComponents.onVariants(androidComponents.selector().all(), variant -> {
            String name = variant.getName();
            variantNames.add(name);


            AndroidTarget target = dslModel.getTargets().findByName(name);
            if (target == null) {
                // The user did not add any target-specific configuration.
                return;
            }

            project.getConfigurations().getByName(name + "Implementation").getDependencies()
                .addAllLater(target.getDependencies().getImplementation().getDependencies());
            project.getConfigurations().getByName(name + "Api").getDependencies()
                .addAllLater(target.getDependencies().getApi().getDependencies());
            project.getConfigurations().getByName(name + "CompileOnly").getDependencies()
                .addAllLater(target.getDependencies().getCompileOnly().getDependencies());
            project.getConfigurations().getByName(name + "RuntimeOnly").getDependencies()
                .addAllLater(target.getDependencies().getRuntimeOnly().getDependencies());
        });

        // This will run after all onVariants calls.
        project.afterEvaluate(p -> dslModel.getTargets().all(target -> {
            if (!variantNames.contains(target.getName())) {
                throw new InvalidUserDataException(String.format(
                    "Configured target '%s' but an Android variant with the same name does not exist.",
                    target.getName()
                ));
            }
        }));
    }

    private static <T> void ifPresent(Property<T> property, Action<T> action) {
        if (property.isPresent()) {
            action.execute(property.get());
        }
    }

}
