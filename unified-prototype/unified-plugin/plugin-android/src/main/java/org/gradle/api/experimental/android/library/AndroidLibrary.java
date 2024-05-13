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

package org.gradle.api.experimental.android.library;

import com.android.build.api.dsl.BaseFlavor;
import com.android.build.api.dsl.CommonExtension;
import org.gradle.api.Action;
import org.gradle.api.experimental.android.nia.Compose;
import org.gradle.api.experimental.android.nia.Feature;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface AndroidLibrary {
    /**
     * @see CommonExtension#getCompileSdk()
     */
    @Restricted
    Property<Integer> getCompileSdk();

    /**
     * @see CommonExtension#getNamespace()
     */
    @Restricted
    Property<String> getNamespace();

    /**
     * @see BaseFlavor#getMinSdk()
     */
    @Restricted
    Property<Integer> getMinSdk();

    /**
     * JDK version to use for compilation.
     */
    @Restricted
    Property<Integer> getJdkVersion();

    /**
     * Controls whether or not to set up Kotlin serialization, applying the plugins
     * and adding any necessary dependencies.
     */
    @Nested
    KotlinSerialization getKotlinSerialization();

    @Configuring
    default void kotlinSerialization(Action<? super KotlinSerialization> action) {
        KotlinSerialization kotlinSerialization = getKotlinSerialization();
        action.execute(kotlinSerialization);
        kotlinSerialization.getEnabled().set(true);
    }

    @Nested
    AndroidLibraryDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super AndroidLibraryDependencies> action) {
        action.execute(getDependencies());
    }

    @Nested
    AndroidLibraryBuildTypes getBuildTypes();

    @Configuring
    default void buildTypes(Action<? super AndroidLibraryBuildTypes> action) {
        action.execute(getBuildTypes());
    }

    @Nested
    Testing getTesting();

    @Configuring
    default void testing(Action<? super Testing> action) {
        action.execute(getTesting());
    }

    @Nested
    Feature getFeature();

    @Configuring
    default void feature(Action<? super Feature> action) {
        Feature feature = getFeature();
        action.execute(feature);
        feature.getEnabled().set(true);
    }

    @Nested
    Compose getCompose();

    @Configuring
    default void compose(Action<? super Compose> action) {
        Compose compose = getCompose();
        action.execute(compose);
        compose.getEnabled().set(true);
    }
}
