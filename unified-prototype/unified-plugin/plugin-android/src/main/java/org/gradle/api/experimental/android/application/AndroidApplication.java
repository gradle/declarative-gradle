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

package org.gradle.api.experimental.android.application;

import com.android.build.api.dsl.ApplicationBaseFlavor;
import org.gradle.api.Action;
import org.gradle.api.experimental.android.AndroidSoftware;
import org.gradle.api.experimental.android.extensions.DataBinding;
import org.gradle.api.experimental.android.extensions.DependencyGuard;
import org.gradle.api.experimental.android.extensions.Firebase;
import org.gradle.api.experimental.android.extensions.ViewBinding;
import org.gradle.api.experimental.android.nia.DimensionStrategy;
import org.gradle.api.experimental.android.nia.Flavors;
import org.gradle.api.internal.plugins.Definition;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.HiddenInDefinition;

public interface AndroidApplication extends AndroidSoftware, Definition<AndroidApplicationBuildModel> {
    /**
     * @see ApplicationBaseFlavor#setVersionName(String)
     */
    Property<String> getVersionName();

    /**
     * @see ApplicationBaseFlavor#setVersionCode(Integer)
     */
    Property<Integer> getVersionCode();

    /**
     * @see ApplicationBaseFlavor#setApplicationId(String)
     */
    Property<String> getApplicationId();

    @Override
    @Nested
    AndroidApplicationDependencies getDependencies();

    @Override
    @Nested
    AndroidApplicationBuildTypes getBuildTypes();

    @Nested
    DependencyGuard getDependencyGuard();

    @Nested
    Firebase getFirebase();

    @Nested
    Flavors getFlavors();

    @Nested
    DimensionStrategy getMissingDimensionStrategy();

    @Nested
    ViewBinding getViewBinding();

    @Nested
    DataBinding getDataBinding();
}
