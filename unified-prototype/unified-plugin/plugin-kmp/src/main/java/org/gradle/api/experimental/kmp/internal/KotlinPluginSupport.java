package org.gradle.api.experimental.kmp.internal;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.experimental.common.BasicDependencies;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension;
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet;

public final class KotlinPluginSupport {
    private KotlinPluginSupport() { /* not instantiable */ }

    public static void linkJavaVersion(HasJavaTarget dslModel, KotlinJvmProjectExtension kotlin) {
        kotlin.jvmToolchain(spec -> spec.getLanguageVersion().set(dslModel.getJavaVersion().map(JavaLanguageVersion::of)));
    }

    public static void linkSourceSetToDependencies(ConfigurationContainer configurations, KotlinSourceSet sourceSet, LibraryDependencies dependencies) {
        linkSourceSetToDependencies(configurations, sourceSet, (BasicDependencies) dependencies);

        configurations.getByName(sourceSet.getApiConfigurationName())
                .getDependencies().addAllLater(dependencies.getApi().getDependencies());
    }

    public static void linkSourceSetToDependencies(ConfigurationContainer configurations, KotlinSourceSet sourceSet, BasicDependencies dependencies) {
        configurations.getByName(sourceSet.getImplementationConfigurationName())
                .getDependencies().addAllLater(dependencies.getImplementation().getDependencies());
        configurations.getByName(sourceSet.getCompileOnlyConfigurationName())
                .getDependencies().addAllLater(dependencies.getCompileOnly().getDependencies());
        configurations.getByName(sourceSet.getRuntimeOnlyConfigurationName())
                .getDependencies().addAllLater(dependencies.getRuntimeOnly().getDependencies());
    }
}
