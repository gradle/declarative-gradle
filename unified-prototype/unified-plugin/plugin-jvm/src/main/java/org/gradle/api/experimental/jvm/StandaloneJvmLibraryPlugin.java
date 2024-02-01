package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.internal.JavaPluginHelper;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.compile.JavaCompile;

/**
 * Creates a declarative {@link JvmLibrary} DSL model, applies the official Jvm plugin,
 * and links the declarative model to the official plugin.
 */
public class StandaloneJvmLibraryPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        AbstractJvmLibrary dslModel = project.getExtensions().create("jvmLibrary", AbstractJvmLibrary.class);

        project.getPlugins().apply(JavaLibraryPlugin.class);

        linkDslModelToPluginLazy(project, dslModel);
    }

    private void linkDslModelToPluginLazy(Project project, AbstractJvmLibrary dslModel) {
        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);

        SourceSet commonSources = JavaPluginHelper.getJavaComponent(project).getMainFeature().getSourceSet();
        commonSources.getJava().srcDirs(project.getLayout().getProjectDirectory().dir("src").dir("common").dir("java"));
        linkSourceSetToDependencies(project, commonSources, dslModel.getDependencies());

        dslModel.getTargets().withType(JavaTarget.class).all(target -> {
            SourceSet sourceSet = java.getSourceSets().create("java" + target.getJavaVersion());
            java.registerFeature("java" + target.getJavaVersion(), feature -> {
                feature.usingSourceSet(sourceSet);
            });

            // Link properties
            project.getTasks().named(sourceSet.getCompileJavaTaskName(), JavaCompile.class, task -> {
                task.getOptions().getRelease().set(target.getJavaVersion());
            });

            // Link dependencies to DSL
            linkSourceSetToDependencies(project, sourceSet, target.getDependencies());

            // Extend common sources
            sourceSet.getJava().srcDirs(commonSources.getAllJava());

            // Extend common dependencies
            project.getConfigurations().getByName(sourceSet.getImplementationConfigurationName())
                .extendsFrom(project.getConfigurations().getByName(commonSources.getImplementationConfigurationName()));
            project.getConfigurations().getByName(sourceSet.getCompileOnlyConfigurationName())
                .extendsFrom(project.getConfigurations().getByName(commonSources.getCompileOnlyConfigurationName()));
            project.getConfigurations().getByName(sourceSet.getRuntimeOnlyConfigurationName())
                .extendsFrom(project.getConfigurations().getByName(commonSources.getRuntimeOnlyConfigurationName()));
            project.getConfigurations().getByName(sourceSet.getApiConfigurationName())
                .extendsFrom(project.getConfigurations().getByName(commonSources.getApiConfigurationName()));
        });
    }

    private void linkSourceSetToDependencies(Project project, SourceSet sourceSet, LibraryDependencies dependencies) {
        project.getConfigurations().getByName(sourceSet.getImplementationConfigurationName())
            .getDependencies().addAllLater(dependencies.getImplementation().getDependencies());
        project.getConfigurations().getByName(sourceSet.getCompileOnlyConfigurationName())
            .getDependencies().addAllLater(dependencies.getCompileOnly().getDependencies());
        project.getConfigurations().getByName(sourceSet.getRuntimeOnlyConfigurationName())
            .getDependencies().addAllLater(dependencies.getRuntimeOnly().getDependencies());
        project.getConfigurations().getByName(sourceSet.getApiConfigurationName())
            .getDependencies().addAllLater(dependencies.getApi().getDependencies());
    }
}
