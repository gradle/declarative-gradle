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

package org.gradle.api.experimental.android.nia;

/*
 * The content for the app can either come from local static data which is useful for demo
 * purposes, or from a production backend server which supplies up-to-date, real content.
 * These two product flavors reflect this behaviour.
 */
public enum NiaFlavor {
    demo(FlavorDimension.contentType, ".demo"),
    prod(FlavorDimension.contentType, null);

    NiaFlavor(FlavorDimension dimension, String applicationIdSuffix) {
        this.dimension = dimension;
        this.applicationIdSuffix = applicationIdSuffix;
    }

    public final FlavorDimension dimension;
    public final String applicationIdSuffix;
}