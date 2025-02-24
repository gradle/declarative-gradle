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

package org.gradle.api.experimental.android.extensions;

import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

// TODO: This might be better split into separate producer/consumer parts
public interface BaselineProfile {
    @Restricted
    Property<Boolean> getEnabled();

    @Restricted
    Property<Boolean> getAutomaticGenerationDuringBuild();

    @Nested
    BaselineProfileDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super BaselineProfileDependencies> action) {
        action.execute(getDependencies());
    }

    @Restricted
    Property<String> getAdditionalManagedDevice();

    @Restricted
    Property<Boolean> getUseConnectedDevices();
}
