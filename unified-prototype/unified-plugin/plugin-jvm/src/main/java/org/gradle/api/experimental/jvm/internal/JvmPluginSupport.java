package org.gradle.api.experimental.jvm.internal;

import org.gradle.api.Project;
import org.gradle.api.experimental.common.ApplicationDependencies;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.api.experimental.jvm.HasJavaTargets;
import org.gradle.api.experimental.jvm.HasJvmApplication;
import org.gradle.api.experimental.jvm.JavaTarget;
import org.gradle.api.file.Directory;
import org.gradle.api.plugins.JavaApplication;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.internal.JavaPluginHelper;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.jvm.toolchain.JavaToolchainService;

import java.util.Collections;

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

    public static void linkJavaVersion(Project project, HasJavaTargets dslModel) {
        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
        java.getToolchain().getLanguageVersion().set(project.provider(() ->
                JavaLanguageVersion.of(dslModel.getTargets().withType(JavaTarget.class).stream().mapToInt(JavaTarget::getJavaVersion).min().getAsInt())
        ));
    }

    public static void linkApplicationMainClass(Project project, HasJvmApplication application) {
        JavaApplication app = project.getExtensions().getByType(JavaApplication.class);
        app.getMainClass().set(application.getMainClass());
    }

    public static SourceSet setupCommonSourceSet(Project project) {
        SourceSet commonSources = JavaPluginHelper.getJavaComponent(project).getMainFeature().getSourceSet();
        Directory srcDir = project.getLayout().getProjectDirectory().dir("src").dir("common").dir("java");
        commonSources.getJava().setSrcDirs(Collections.singleton(srcDir));
        return commonSources;
    }

    public static SourceSet createTargetSourceSet(Project project,
                                                  JavaTarget target,
                                                  SourceSet commonSources,
                                                  JavaToolchainService javaToolchainService) {
        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
        SourceSet sourceSet = java.getSourceSets().create("java" + target.getJavaVersion());
        java.registerFeature("java" + target.getJavaVersion(), feature -> {
            feature.usingSourceSet(sourceSet);
        });

        // Link properties
        project.getTasks().named(sourceSet.getCompileJavaTaskName(), JavaCompile.class, task -> {
            task.getJavaCompiler().set(javaToolchainService.compilerFor(spec -> spec.getLanguageVersion().set(JavaLanguageVersion.of(target.getJavaVersion()))));
        });

        // Depend on common sources
        project.getConfigurations().getByName(sourceSet.getImplementationConfigurationName())
                .getDependencies().add(project.getDependencies().create(commonSources.getOutput()));

        // Extend common dependencies
        project.getConfigurations().getByName(sourceSet.getImplementationConfigurationName())
                .extendsFrom(project.getConfigurations().getByName(commonSources.getImplementationConfigurationName()));
        project.getConfigurations().getByName(sourceSet.getCompileOnlyConfigurationName())
                .extendsFrom(project.getConfigurations().getByName(commonSources.getCompileOnlyConfigurationName()));
        project.getConfigurations().getByName(sourceSet.getRuntimeOnlyConfigurationName())
                .extendsFrom(project.getConfigurations().getByName(commonSources.getRuntimeOnlyConfigurationName()));

        // Assemble includes all targets
        project.getTasks().named("assemble").configure(task -> task.dependsOn(sourceSet.getOutput()));

        return sourceSet;
    }

}
