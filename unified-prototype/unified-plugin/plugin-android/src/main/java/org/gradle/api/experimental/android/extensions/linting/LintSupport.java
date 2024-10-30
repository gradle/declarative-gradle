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

package org.gradle.api.experimental.android.extensions.linting;

import com.android.build.api.dsl.ApplicationExtension;
import com.android.build.api.dsl.LibraryExtension;
import org.gradle.api.Project;
import com.android.build.api.dsl.Lint;
import org.gradle.api.experimental.common.extensions.HasLinting;

public final class LintSupport {
    private LintSupport() { /* not instantiable */ }

    public static void configureLint(Project project, HasLinting hasLinting) {
        if (project.getPlugins().hasPlugin("com.android.application")) {
            ApplicationExtension extension = project.getExtensions().getByType(ApplicationExtension.class);
            extension.getLint().setXmlReport(hasLinting.getLint().getXmlReport().get());
            extension.getLint().setCheckDependencies(hasLinting.getLint().getCheckDependencies().get());
        } else if (project.getPlugins().hasPlugin("com.android.library")) {
            LibraryExtension extension = project.getExtensions().getByType(LibraryExtension.class);
            extension.getLint().setXmlReport(hasLinting.getLint().getXmlReport().get());
            extension.getLint().setCheckDependencies(hasLinting.getLint().getCheckDependencies().get());
        } else {
            project.getPlugins().apply("com.android.lint");
            Lint extension = project.getExtensions().getByType(Lint.class);
            extension.setXmlReport(hasLinting.getLint().getXmlReport().get());
            extension.setCheckDependencies(hasLinting.getLint().getCheckDependencies().get());
        }
    }
}
