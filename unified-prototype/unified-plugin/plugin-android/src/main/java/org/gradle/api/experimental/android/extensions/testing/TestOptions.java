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

package org.gradle.api.experimental.android.extensions.testing;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface TestOptions {
    @Restricted
    Property<Boolean> getIncludeAndroidResources();

    @Restricted
    Property<Boolean> getReturnDefaultValues();

    @Restricted
    Property<String> getTestInstrumentationRunner();

    @Nested
    NamedDomainObjectContainer<ManagedDevice> getManagedDevices();
}
