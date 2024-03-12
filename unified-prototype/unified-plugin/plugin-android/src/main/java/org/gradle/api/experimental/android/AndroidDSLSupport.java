/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.experimental.android;

import com.android.build.api.dsl.LibraryExtension;
import com.android.build.api.variant.AndroidComponentsExtension;
import org.gradle.api.Action;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AndroidDSLSupport {
    private AndroidDSLSupport() { /* not instantiable */ }

    /**
     * Performs linking actions that must occur within an afterEvaluate block.
     */
    public static void linkDslModelToPlugin(Project project, AndroidLibrary dslModel) {
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
    public static void linkDslModelToPluginLazy(Project project, AndroidLibrary dslModel) {
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
            AndroidTarget target = getTarget(dslModel, variant.getName());
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

            AndroidTarget target = getTarget(dslModel, variant.getName());
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
    }

    private static <T> void ifPresent(Property<T> property, Action<T> action) {
        if (property.isPresent()) {
            action.execute(property.get());
        }
    }

    private static Set<AndroidTarget> getTargets(AndroidLibrary dslModel) {
        return Set.of(dslModel.getTargets().getDebug(), dslModel.getTargets().getRelease());
    }

    @Nullable
    private static AndroidTarget getTarget(AndroidLibrary dslModel, String name) {
        return getTargets(dslModel).stream()
                .filter(t -> Objects.equals(t.getName(), name))
                .findFirst()
                .orElse(null);
    }
}
