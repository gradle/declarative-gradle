package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.internal.JavaPluginHelper;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.jvm.toolchain.JavaToolchainService;

import javax.inject.Inject;

/**
 * Creates a declarative {@link JvmLibrary} DSL model, applies the official Jvm plugin,
 * and links the declarative model to the official plugin.
 */
abstract public class StandaloneJvmLibraryPlugin implements Plugin<Project> {
    @SoftwareType(name= "jvmLibrary", modelPublicType=JvmLibrary.class)
    abstract public AbstractJvmLibrary getJvmLibrary();

    @Override
    public void apply(Project project) {
        AbstractJvmLibrary dslModel = getJvmLibrary();

        project.getPlugins().apply(JavaLibraryPlugin.class);

        linkDslModelToPlugin(project, dslModel);
    }

    @Inject
    protected abstract JavaToolchainService getJavaToolchainService();

    private void linkDslModelToPlugin(Project project, AbstractJvmLibrary dslModel) {
        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);

        SourceSet commonSources = JavaPluginHelper.getJavaComponent(project).getMainFeature().getSourceSet();
        commonSources.getJava().srcDirs(project.getLayout().getProjectDirectory().dir("src").dir("common").dir("java"));
        JvmPluginSupport.linkSourceSetToDependencies(project, commonSources, dslModel.getDependencies());

        java.getToolchain().getLanguageVersion().set(project.provider(() ->
            JavaLanguageVersion.of(dslModel.getTargets().withType(JavaTarget.class).stream().mapToInt(JavaTarget::getJavaVersion).min().getAsInt())
        ));

        dslModel.getTargets().withType(JavaTarget.class).all(target -> {
            SourceSet sourceSet = java.getSourceSets().create("java" + target.getJavaVersion());
            java.registerFeature("java" + target.getJavaVersion(), feature -> {
                feature.usingSourceSet(sourceSet);
            });

            // Link properties
            project.getTasks().named(sourceSet.getCompileJavaTaskName(), JavaCompile.class, task -> {
                task.getJavaCompiler().set(getJavaToolchainService().compilerFor(spec -> spec.getLanguageVersion().set(JavaLanguageVersion.of(target.getJavaVersion()))));
            });

            // Link dependencies to DSL
            JvmPluginSupport.linkSourceSetToDependencies(project, sourceSet, target.getDependencies());

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
}
