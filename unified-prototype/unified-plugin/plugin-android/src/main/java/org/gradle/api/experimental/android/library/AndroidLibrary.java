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
     * Whether or not to set up Kotlin serialization, applying the plugins and adding any necessary dependencies.
     */
    @Restricted
    Property<Boolean> getIncludeKotlinSerialization();

    /**
     * Whether or not to set up Jacoco support.
     */
    @Restricted
    Property<Boolean> getConfigureJacoco();

    @Nested
    AndroidLibraryDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super AndroidLibraryDependencies> action) {
        action.execute(getDependencies());
    }

    @Nested
    TestOptions getTestOptions();

    @Configuring
    default void testOptions(Action<? super TestOptions> action) {
        action.execute(getTestOptions());
    }

    @Nested
    AndroidLibraryBuildTypes getBuildTypes();

    @Configuring
    default void buildTypes(Action<? super AndroidLibraryBuildTypes> action) {
        action.execute(getBuildTypes());
    }
}
