package org.gradle.api.experimental.kmp;

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension;

abstract public class DefaultKotlinMultiplatformBuildModel implements KotlinMultiplatformBuildModel {
    private KotlinMultiplatformExtension kotlinMultiplatformExtension;

    @Override
    public KotlinMultiplatformExtension getKotlinMultiplatformExtension() {
        return kotlinMultiplatformExtension;
    }

    public void setKotlinMultiplatformExtension(KotlinMultiplatformExtension kotlinMultiplatformExtension) {
        this.kotlinMultiplatformExtension = kotlinMultiplatformExtension;
    }
}
