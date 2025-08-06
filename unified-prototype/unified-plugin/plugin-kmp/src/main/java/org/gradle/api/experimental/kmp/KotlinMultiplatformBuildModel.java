package org.gradle.api.experimental.kmp;

import org.gradle.api.experimental.common.HasGroupAndVersion;
import org.gradle.api.internal.plugins.BuildModel;
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension;

public interface KotlinMultiplatformBuildModel extends BuildModel, HasGroupAndVersion {
    KotlinMultiplatformExtension getKotlinMultiplatformExtension();
}
