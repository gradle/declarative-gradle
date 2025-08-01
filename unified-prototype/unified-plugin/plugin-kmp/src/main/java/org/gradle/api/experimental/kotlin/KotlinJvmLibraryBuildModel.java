package org.gradle.api.experimental.kotlin;

import org.gradle.api.experimental.jvm.JavaBuildModel;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension;

public interface KotlinJvmLibraryBuildModel extends JavaBuildModel {
    KotlinJvmProjectExtension getKotlinJvmExtension();
}
