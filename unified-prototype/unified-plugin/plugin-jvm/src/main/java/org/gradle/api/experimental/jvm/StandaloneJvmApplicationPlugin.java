package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.jvm.toolchain.JavaToolchainService;

import javax.inject.Inject;

/**
 * Creates a declarative {@link JvmApplication} DSL model, applies the official Jvm plugin,
 * and links the declarative model to the official plugin.
 */
abstract public class StandaloneJvmApplicationPlugin implements Plugin<Project> {
    @SoftwareType(name = "jvmApplication", modelPublicType = JvmApplication.class)
    abstract public JvmApplication getJvmLibrary();

    @Override
    public void apply(Project project) {
        JvmApplication dslModel = getJvmLibrary();

        project.getPlugins().apply(ApplicationPlugin.class);

        linkDslModelToPlugin(project, dslModel);
    }

    @Inject
    protected abstract JavaToolchainService getJavaToolchainService();

    private void linkDslModelToPlugin(Project project, JvmApplication dslModel) {
        SourceSet commonSources = JvmPluginSupport.setupCommonSourceSet(project);
        JvmPluginSupport.linkSourceSetToDependencies(project, commonSources, dslModel.getDependencies());

        JvmPluginSupport.linkJavaVersion(project, dslModel);
        JvmPluginSupport.linkApplicationMainClass(project, dslModel);
        dslModel.getTargets().withType(JavaTarget.class).all(target -> {
            SourceSet sourceSet = JvmPluginSupport.createTargetSourceSet(project, target, commonSources, getJavaToolchainService());

            // Link dependencies to DSL
            JvmPluginSupport.linkSourceSetToDependencies(project, sourceSet, target.getDependencies());
        });
    }
}
