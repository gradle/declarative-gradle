package org.gradle.api.experimental.kotlin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.plugins.software.SoftwareType;

/**
 * Creates a declarative {@link KotlinJvmApplication} DSL model, applies the official Kotlin and application plugin,
 * and links the declarative model to the official plugin.
 */
abstract public class StandaloneKotlinJvmApplicationPlugin implements Plugin<Project> {
    @SoftwareType(name = "kotlinJvmApplication", modelPublicType = KotlinJvmApplication.class)
    abstract public KotlinJvmApplication getApplication();

    @Override
    public void apply(Project project) {
    }
}
