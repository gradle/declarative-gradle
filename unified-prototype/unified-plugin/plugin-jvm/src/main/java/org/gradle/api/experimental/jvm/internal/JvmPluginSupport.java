package org.gradle.api.experimental.jvm.internal;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.DependencyScopeConfiguration;
import org.gradle.api.artifacts.ResolvableConfiguration;
import org.gradle.api.artifacts.dsl.DependencyFactory;
import org.gradle.api.experimental.common.ApplicationDependencies;
import org.gradle.api.experimental.common.BasicDependencies;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.experimental.java.checkstyle.CheckstyleDefinition;
import org.gradle.api.experimental.jvm.HasJavaTarget;
import org.gradle.api.experimental.jvm.HasJavaTargets;
import org.gradle.api.experimental.jvm.HasJvmApplication;
import org.gradle.api.experimental.jvm.JavaTarget;
import org.gradle.api.experimental.jvm.extensions.testing.TestDependencies;
import org.gradle.api.experimental.jvm.extensions.testing.Testing;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.plugins.JavaApplication;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.quality.Checkstyle;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.jvm.toolchain.JavaToolchainService;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

import java.util.Collections;

public class JvmPluginSupport {
    public static void linkMainSourceSourceSetDependencies(LibraryDependencies dependencies, JavaPluginExtension javaExtension, ConfigurationContainer configurations) {
        linkSourceSetToDependencies(javaExtension.getSourceSets().getByName("main"), dependencies, configurations);
    }

    public static void linkMainSourceSourceSetDependencies(ApplicationDependencies dependencies, JavaPluginExtension javaExtension, ConfigurationContainer configurations) {
        linkSourceSetToDependencies(javaExtension.getSourceSets().getByName("main"), dependencies, configurations);
    }

    public static void linkTestSourceSourceSetDependencies(TestDependencies dependencies, JavaPluginExtension javaExtension, ConfigurationContainer configurations) {
        linkSourceSetToDependencies(javaExtension.getSourceSets().getByName("test"), dependencies, configurations);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void linkSourceSetToDependencies(SourceSet sourceSet, BasicDependencies dependencies, ConfigurationContainer configurations) {
        configurations.getByName(sourceSet.getImplementationConfigurationName())
            .getDependencies().addAllLater(dependencies.getImplementation().getDependencies());
        configurations.getByName(sourceSet.getCompileOnlyConfigurationName())
            .getDependencies().addAllLater(dependencies.getCompileOnly().getDependencies());
        configurations.getByName(sourceSet.getRuntimeOnlyConfigurationName())
            .getDependencies().addAllLater(dependencies.getRuntimeOnly().getDependencies());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void linkSourceSetToDependencies(SourceSet sourceSet, LibraryDependencies dependencies, ConfigurationContainer configurations) {
        linkSourceSetToDependencies(sourceSet, (BasicDependencies) dependencies, configurations);
        configurations
                .named(name -> name.equals(sourceSet.getApiConfigurationName()))
                .configureEach(conf ->
                    conf.getDependencies().addAllLater(dependencies.getApi().getDependencies())
                );
    }

    public static void linkJavaVersion(HasJavaTarget dslModel, JavaPluginExtension javaExtension) {
        javaExtension.getToolchain().getLanguageVersion().set(dslModel.getJavaVersion().map(JavaLanguageVersion::of));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void linkJavaVersion(HasJavaTargets dslModel, JavaPluginExtension javaExtension, ProviderFactory providerFactory) {
        javaExtension.getToolchain().getLanguageVersion().set(providerFactory.provider(() ->
                JavaLanguageVersion.of(dslModel.getTargets().withType(JavaTarget.class).stream().mapToInt(JavaTarget::getJavaVersion).min().getAsInt())
        ));
    }

    public static void linkApplicationMainClass(HasJvmApplication application, JavaApplication javaApplication) {
        javaApplication.getMainClass().set(application.getMainClass());
        javaApplication.setApplicationDefaultJvmArgs(application.getJvmArguments().get());
    }

    public static void setupCommonSourceSet(SourceSet commonSources, ProjectLayout layout) {
        Directory srcDir = layout.getProjectDirectory().dir("src").dir("common").dir("java");
        commonSources.getJava().setSrcDirs(Collections.singleton(srcDir));
    }

    public static SourceSet createTargetSourceSet(JavaTarget target,
                                                  SourceSet commonSources,
                                                  JavaToolchainService javaToolchainService,
                                                  JavaPluginExtension javaExtension,
                                                  TaskContainer tasks,
                                                  ConfigurationContainer configurations,
                                                  DependencyFactory dependencyFactory) {

        SourceSet sourceSet = javaExtension.getSourceSets().create("java" + target.getJavaVersion());
        javaExtension.registerFeature("java" + target.getJavaVersion(), feature -> {
            feature.usingSourceSet(sourceSet);
        });

        // Link properties
        tasks.named(sourceSet.getCompileJavaTaskName(), JavaCompile.class, task -> {
            task.getJavaCompiler().set(javaToolchainService.compilerFor(spec -> spec.getLanguageVersion().set(JavaLanguageVersion.of(target.getJavaVersion()))));
        });

        // Depend on common sources
        configurations.getByName(sourceSet.getImplementationConfigurationName())
                .getDependencies().add(dependencyFactory.create(commonSources.getOutput()));

        // Extend common dependencies
        configurations.getByName(sourceSet.getImplementationConfigurationName())
                .extendsFrom(configurations.getByName(commonSources.getImplementationConfigurationName()));
        configurations.getByName(sourceSet.getCompileOnlyConfigurationName())
                .extendsFrom(configurations.getByName(commonSources.getCompileOnlyConfigurationName()));
        configurations.getByName(sourceSet.getRuntimeOnlyConfigurationName())
                .extendsFrom(configurations.getByName(commonSources.getRuntimeOnlyConfigurationName()));

        // Assemble includes all targets
        tasks.named("assemble").configure(task -> task.dependsOn(sourceSet.getOutput()));

        return sourceSet;
    }

    public static void linkTestJavaVersion(Testing testing, JavaToolchainService toolchains, TaskContainer tasks) {
        tasks.withType(Test.class).named("test").configure(task -> {
            task.getJavaLauncher().set(toolchains.launcherFor(spec -> spec.getLanguageVersion().set(testing.getTestJavaVersion().map(JavaLanguageVersion::of))));
        });
    }
}
