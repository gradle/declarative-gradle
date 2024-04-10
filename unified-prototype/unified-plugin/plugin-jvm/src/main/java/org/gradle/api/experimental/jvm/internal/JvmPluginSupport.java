package org.gradle.api.experimental.jvm.internal;

import org.gradle.api.Project;
import org.gradle.api.experimental.common.ApplicationDependencies;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.tasks.SourceSet;

public class JvmPluginSupport {
    public static void linkSourceSetToDependencies(Project project, SourceSet sourceSet, LibraryDependencies dependencies) {
        project.getConfigurations().getByName(sourceSet.getImplementationConfigurationName())
                .getDependencies().addAllLater(dependencies.getImplementation().getDependencies());
        project.getConfigurations().getByName(sourceSet.getCompileOnlyConfigurationName())
                .getDependencies().addAllLater(dependencies.getCompileOnly().getDependencies());
        project.getConfigurations().getByName(sourceSet.getRuntimeOnlyConfigurationName())
                .getDependencies().addAllLater(dependencies.getRuntimeOnly().getDependencies());
        project.getConfigurations().getByName(sourceSet.getApiConfigurationName())
                .getDependencies().addAllLater(dependencies.getApi().getDependencies());
    }

    public static void linkSourceSetToDependencies(Project project, SourceSet sourceSet, ApplicationDependencies dependencies) {
        project.getConfigurations().getByName(sourceSet.getImplementationConfigurationName())
                .getDependencies().addAllLater(dependencies.getImplementation().getDependencies());
        project.getConfigurations().getByName(sourceSet.getCompileOnlyConfigurationName())
                .getDependencies().addAllLater(dependencies.getCompileOnly().getDependencies());
        project.getConfigurations().getByName(sourceSet.getRuntimeOnlyConfigurationName())
                .getDependencies().addAllLater(dependencies.getRuntimeOnly().getDependencies());
    }

}
