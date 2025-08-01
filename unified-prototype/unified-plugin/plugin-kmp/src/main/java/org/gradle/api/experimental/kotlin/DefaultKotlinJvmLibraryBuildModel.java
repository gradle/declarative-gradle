package org.gradle.api.experimental.kotlin;

import org.gradle.api.experimental.jvm.DefaultJavaBuildModel;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension;

public class DefaultKotlinJvmLibraryBuildModel extends DefaultJavaBuildModel implements KotlinJvmLibraryBuildModel {

    private KotlinJvmProjectExtension kotlinJvmExtension;

    @Override
    public KotlinJvmProjectExtension getKotlinJvmExtension() {
        return kotlinJvmExtension;
    }

    public void setKotlinJvmExtension(KotlinJvmProjectExtension kotlinJvmExtension) {
        this.kotlinJvmExtension = kotlinJvmExtension;
    }
}
