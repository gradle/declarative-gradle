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
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

@CacheableTask
public abstract class GenerateBadgingTask extends DefaultTask {
    @OutputFile
    public abstract RegularFileProperty getBadging();

    @PathSensitive(PathSensitivity.NONE)
    @InputFile
    public abstract RegularFileProperty getApk();

    @PathSensitive(PathSensitivity.NONE)
    @InputFile
    public abstract RegularFileProperty getAapt2Executable();

    @Inject
    public abstract ExecOperations getExecOperations();

    @TaskAction
    public void taskAction() {
        getExecOperations().exec(execSpec -> {
            execSpec.commandLine(
                    getAapt2Executable().get().getAsFile().getAbsolutePath(),
                    "dump",
                    "badging",
                    getApk().get().getAsFile().getAbsolutePath()
            );

            try {
                OutputStream os = new FileOutputStream(getBadging().get().getAsFile());
                execSpec.setStandardOutput(os);
                // Stream automatically closed after the process completes
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
