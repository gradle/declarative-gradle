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

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static com.google.common.truth.Truth.assertWithMessage;

@CacheableTask
public abstract class CheckBadgingTask extends DefaultTask {

    // In order for the task to be up-to-date when the inputs have not changed,
    // the task must declare an output, even if it's not used. Tasks with no
    // output are always run regardless of whether the inputs changed
    @OutputDirectory
    public abstract DirectoryProperty getOutput();

    @PathSensitive(PathSensitivity.NONE)
    @InputFile
    public abstract RegularFileProperty getGoldenBadging();

    @PathSensitive(PathSensitivity.NONE)
    @InputFile
    public abstract RegularFileProperty getGeneratedBadging();

    @Input
    public abstract Property<String> getUpdateBadgingTaskName();

    @Override
    public String getGroup() {
        return LifecycleBasePlugin.VERIFICATION_GROUP;
    }

    @TaskAction
    public void taskAction() {
        try {
            String goldenBadgingContent = new String(Files.readAllBytes(Paths.get(getGoldenBadging().get().getAsFile().getAbsolutePath())));
            String generatedBadgingContent = new String(Files.readAllBytes(Paths.get(getGeneratedBadging().get().getAsFile().getAbsolutePath())));
            assertWithMessage(
                    "Generated badging is different from golden badging! " +
                            "If this change is intended, run ./gradlew " + getUpdateBadgingTaskName().get()
            ).that(generatedBadgingContent).isEqualTo(goldenBadgingContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read badging files", e);
        }
    }
}
