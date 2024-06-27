package org.gradle.api.experimental.java;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.common.CliApplicationConventionsPlugin;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.plugins.ApplicationPlugin;

/**
 * Creates a declarative {@link JavaApplication} DSL model, applies the official Java application plugin,
 * and links the declarative model to the official plugin.
 */
abstract public class StandaloneJavaApplicationPlugin implements Plugin<Project> {

    public static final String JAVA_APPLICATION = "javaApplication";

    @SoftwareType(name = JAVA_APPLICATION, modelPublicType = JavaApplication.class)
    abstract public JavaApplication getApplication();

    @Override
    public void apply(Project project) {
        JavaApplication dslModel = getApplication();
        project.getExtensions().add(JAVA_APPLICATION, dslModel);

        project.getPlugins().apply(ApplicationPlugin.class);
        project.getPlugins().apply(CliApplicationConventionsPlugin.class);

        linkDslModelToPlugin(project, dslModel);
    }

    private void linkDslModelToPlugin(Project project, JavaApplication dslModel) {
        JvmPluginSupport.linkJavaVersion(project, dslModel);
        JvmPluginSupport.linkApplicationMainClass(project, dslModel);
        JvmPluginSupport.linkMainSourceSourceSetDependencies(project, dslModel.getDependencies());

        dslModel.getRunTasks().add(project.getTasks().named("run"));
    }
}
