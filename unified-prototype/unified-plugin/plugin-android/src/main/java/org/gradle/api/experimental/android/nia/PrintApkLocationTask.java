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

import com.android.build.api.variant.BuiltArtifacts;
import com.android.build.api.variant.BuiltArtifactsLoader;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.internal.impldep.com.google.common.collect.Iterables;
import org.gradle.work.DisableCachingByDefault;

import java.io.File;
import java.nio.file.Path;

@DisableCachingByDefault(because = "Prints output") // TODO: Not converted
public abstract class PrintApkLocationTask extends DefaultTask {
    @PathSensitive(PathSensitivity.RELATIVE)
    @InputDirectory
    public abstract DirectoryProperty getApkFolder();

    @PathSensitive(PathSensitivity.RELATIVE)
    @InputFiles
    public abstract ListProperty<Directory> getSources();

    @Internal
    public abstract Property<BuiltArtifactsLoader> getBuiltArtifactsLoader();

    @Input
    public abstract Property<String> getVariantName();

    @TaskAction
    public void taskAction() {
        if (!getSources().isPresent()) {
            throw new RuntimeException("Cannot check androidTest sources");
        }
        boolean hasFiles = getSources().get().stream().anyMatch(directory ->
            directory.getAsFileTree().getFiles().stream().anyMatch(it -> {
                return it.isFile() && !it.getParentFile().getPath().contains("build${File.separator}generated");
            })
        );

        // Don't print APK location if there are no androidTest source files
        if (hasFiles) {
            if (!getBuiltArtifactsLoader().isPresent()) {
                throw new RuntimeException("Cannot load APKs");
            }
            BuiltArtifacts builtArtifacts = getBuiltArtifactsLoader().get().load(getApkFolder().get());
            if (builtArtifacts == null) {
                throw new RuntimeException("Cannot load APKs");
            }
            if (builtArtifacts.getElements().size() != 1) {
                throw new RuntimeException("Expected one APK !");
            }
            Path apk = new File(Iterables.getOnlyElement(builtArtifacts.getElements()).getOutputFile()).toPath();
            System.out.println(apk);
        }
    }
}
