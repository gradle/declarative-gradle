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

public class DeviceConfig {
    private final String device;
    private final int apiLevel;
    private final String systemImageSource;

    public DeviceConfig(String device, int apiLevel, String systemImageSource) {
        this.device = device;
        this.apiLevel = apiLevel;
        this.systemImageSource = systemImageSource;
    }

    public String getTaskName() {
        return new StringBuilder()
                .append(device.toLowerCase().replace(" ", ""))
                .append("api")
                .append(Integer.toString(apiLevel))
                .append(systemImageSource.replace("-", ""))
                .toString();
    }

    public String getDevice() {
        return device;
    }

    public int getApiLevel() {
        return apiLevel;
    }

    public String getSystemImageSource() {
        return systemImageSource;
    }
}