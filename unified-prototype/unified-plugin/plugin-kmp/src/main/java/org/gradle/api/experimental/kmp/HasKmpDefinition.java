package org.gradle.api.experimental.kmp;

import org.gradle.api.experimental.common.HasGroupAndVersion;
import org.gradle.features.binding.Definition;

/**
 * The shared definition interface for KMP application and library.
 */
@SuppressWarnings("UnstableApiUsage")
public interface HasKmpDefinition<B extends KotlinMultiplatformBuildModel> extends HasGroupAndVersion, Definition<B> {
}
