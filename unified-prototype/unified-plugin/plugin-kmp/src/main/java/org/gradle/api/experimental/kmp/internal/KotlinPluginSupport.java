package org.gradle.api.experimental.kmp.internal;

import org.gradle.api.Project;
import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension;

public class KotlinPluginSupport {
    public static void linkJavaVersion(Project project, HasJavaTarget dslModel) {
        KotlinJvmProjectExtension kotlin = project.getExtensions().getByType(KotlinJvmProjectExtension.class);
        kotlin.jvmToolchain(spec -> spec.getLanguageVersion().set(dslModel.getJavaVersion().map(JavaLanguageVersion::of)));
    }
}
