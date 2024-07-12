package org.gradle.api.experimental.java;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.common.CliApplicationConventionsPlugin;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.jvm.JvmTestSuite;
import org.gradle.jvm.toolchain.JavaToolchainService;
import org.gradle.testing.base.TestingExtension;

import javax.inject.Inject;

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

        project.getExtensions().getByType(TestingExtension.class).getSuites().withType(JvmTestSuite.class).named("test").configure(testSuite -> {
            testSuite.useJUnitJupiter();
        });

        linkDslModelToPlugin(project, dslModel);
    }

    @Inject
    protected abstract JavaToolchainService getJavaToolchainService();

    private void linkDslModelToPlugin(Project project, JavaApplication dslModel) {
        JvmPluginSupport.linkJavaVersion(project, dslModel);
        JvmPluginSupport.linkApplicationMainClass(project, dslModel);
        JvmPluginSupport.linkMainSourceSourceSetDependencies(project, dslModel.getDependencies());
        JvmPluginSupport.linkTestJavaVersion(project, getJavaToolchainService(), dslModel.getTesting());
        JvmPluginSupport.linkTestSourceSourceSetDependencies(project, dslModel.getTesting().getDependencies());

        dslModel.getRunTasks().add(project.getTasks().named("run"));
    }
}
