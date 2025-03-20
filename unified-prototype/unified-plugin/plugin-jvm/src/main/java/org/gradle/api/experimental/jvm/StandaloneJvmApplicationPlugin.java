package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.common.CliApplicationConventionsPlugin;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.toolchain.JavaToolchainService;

import javax.inject.Inject;

/**
 * Creates a declarative {@link JvmApplication} DSL model, applies the official Jvm plugin,
 * and links the declarative model to the official plugin.
 */
public abstract class StandaloneJvmApplicationPlugin implements Plugin<Project> {

    public static final String JVM_APPLICATION = "jvmApplication";

    @SuppressWarnings("UnstableApiUsage")
    @SoftwareType(name = JVM_APPLICATION, modelPublicType = JvmApplication.class)
    public abstract JvmApplication getJvmApplication();

    @Override
    public void apply(Project project) {
        JvmApplication dslModel = getJvmApplication();

        project.getPlugins().apply(ApplicationPlugin.class);
        project.getPlugins().apply(CliApplicationConventionsPlugin.class);

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

            // Create a run task
            TaskProvider<JavaExec> runTask = project.getTasks().register(sourceSet.getTaskName("run", null), JavaExec.class, task -> {
                task.getMainClass().set(dslModel.getMainClass());
                task.getJvmArguments().set(dslModel.getJvmArguments());
                task.setClasspath(sourceSet.getRuntimeClasspath());
            });
            dslModel.getRunTasks().add(runTask);
        });
    }
}
