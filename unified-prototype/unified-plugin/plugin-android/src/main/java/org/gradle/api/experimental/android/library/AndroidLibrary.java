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

import org.gradle.api.Action;
import org.gradle.api.experimental.android.AndroidSoftware;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.experimental.android.extensions.Protobuf;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface AndroidLibrary extends AndroidSoftware {
    @Override
    @Nested
    AndroidLibraryDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super AndroidLibraryDependencies> action) {
        action.execute(getDependencies());
    }

    @Override
    @Nested
    AndroidLibraryBuildTypes getBuildTypes();

    @Configuring
    default void buildTypes(Action<? super AndroidLibraryBuildTypes> action) {
        action.execute(getBuildTypes());
    }

    @Restricted
    ListProperty<RegularFile> getConsumerProguardFiles();

    @Nested
    Protobuf getProtobuf();

    @Configuring
    default void protobuf(Action<? super Protobuf> action) {
        action.execute(getProtobuf());
    }

    /**
     * Flag to enable/disable generation of the `BuildConfig` class.
     * <p>
     * Default value is `false`.
     */
    @Restricted
    Property<Boolean> getBuildConfig();
}
