package org.gradle.api.experimental.kotlin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension;

/**
 * Creates a declarative {@link KotlinJvmApplication} DSL model, applies the official Kotlin and application plugin,
 * and links the declarative model to the official plugin.
 */
abstract public class StandaloneKotlinJvmLibraryPlugin implements Plugin<Project> {
    @SoftwareType(name = "kotlinJvmLibrary", modelPublicType = KotlinJvmLibrary.class)
    abstract public KotlinJvmLibrary getLibrary();

    @Override
    public void apply(Project project) {
        KotlinJvmLibrary dslModel = getLibrary();

        project.getPlugins().apply("org.jetbrains.kotlin.jvm");

        linkDslModelToPluginLazy(project, dslModel);
    }

    private void linkDslModelToPluginLazy(Project project, KotlinJvmLibrary dslModel) {
        KotlinJvmProjectExtension kotlin = project.getExtensions().getByType(KotlinJvmProjectExtension.class);
        kotlin.jvmToolchain(spec -> spec.getLanguageVersion().set(dslModel.getJavaVersion().map(JavaLanguageVersion::of)));
    }
}
