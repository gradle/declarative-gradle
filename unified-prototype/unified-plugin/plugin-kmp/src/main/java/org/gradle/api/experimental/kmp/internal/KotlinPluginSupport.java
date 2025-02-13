package org.gradle.api.experimental.kmp.internal;

import org.gradle.api.Project;
import org.gradle.api.experimental.common.BasicDependencies;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension;
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet;

public final class KotlinPluginSupport {
    private KotlinPluginSupport() { /* not instantiable */ }

    public static void linkJavaVersion(Project project, HasJavaTarget dslModel) {
        KotlinJvmProjectExtension kotlin = project.getExtensions().getByType(KotlinJvmProjectExtension.class);
        kotlin.jvmToolchain(spec -> spec.getLanguageVersion().set(dslModel.getJavaVersion().map(JavaLanguageVersion::of)));
    }

    public static void linkSourceSetToDependencies(Project project, KotlinSourceSet sourceSet, LibraryDependencies dependencies) {
        linkSourceSetToDependencies(project, sourceSet, (BasicDependencies) dependencies);

        project.getConfigurations().getByName(sourceSet.getApiConfigurationName())
                .getDependencies().addAllLater(dependencies.getApi().getDependencies());
    }

    public static void linkSourceSetToDependencies(Project project, KotlinSourceSet sourceSet, BasicDependencies dependencies) {
        project.getConfigurations().getByName(sourceSet.getImplementationConfigurationName())
                .getDependencies().addAllLater(dependencies.getImplementation().getDependencies());
        project.getConfigurations().getByName(sourceSet.getCompileOnlyConfigurationName())
                .getDependencies().addAllLater(dependencies.getCompileOnly().getDependencies());
        project.getConfigurations().getByName(sourceSet.getRuntimeOnlyConfigurationName())
                .getDependencies().addAllLater(dependencies.getRuntimeOnly().getDependencies());
    }
}
