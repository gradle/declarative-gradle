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

import org.gradle.api.artifacts.dsl.DependencyCollector;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@SuppressWarnings("UnstableApiUsage")
@Restricted
public interface AndroidLibraryDependencies extends LibraryDependencies {
    DependencyCollector getKsp();
    DependencyCollector getCoreLibraryDesugaring();
    DependencyCollector getDebugImplementation();

    // TESTING: TODO: Move these into the Testing block (give it its own dependencies)?
    DependencyCollector getTestImplementation();
    DependencyCollector getAndroidTestImplementation();
}
