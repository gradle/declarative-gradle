package org.gradle.api.experimental.jvm.internal;

import org.gradle.api.Project;
import org.gradle.api.experimental.common.ApplicationDependencies;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.api.experimental.jvm.HasJvmApplication;
import org.gradle.api.plugins.JavaApplication;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

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

    public static void linkMainSourceSourceSetDependencies(Project project, LibraryDependencies dependencies) {
        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
        linkSourceSetToDependencies(project, java.getSourceSets().getByName("main"), dependencies);
    }

    public static void linkMainSourceSourceSetDependencies(Project project, ApplicationDependencies dependencies) {
        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
        linkSourceSetToDependencies(project, java.getSourceSets().getByName("main"), dependencies);
    }

    public static void linkJavaVersion(Project project, HasJavaTarget dslModel) {
        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
        java.getToolchain().getLanguageVersion().set(dslModel.getJavaVersion().map(JavaLanguageVersion::of));
    }

    public static void linkApplicationMainClass(Project project, HasJvmApplication application) {
        JavaApplication app = project.getExtensions().getByType(JavaApplication.class);
        app.getMainClass().set(application.getMainClass());
    }

}
