package org.gradle.api.experimental.java;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.jvm.JvmTestSuite;
import org.gradle.jvm.toolchain.JavaToolchainService;
import org.gradle.testing.base.TestingExtension;

import javax.inject.Inject;

/**
 * Creates a declarative {@link JavaLibrary} DSL model, applies the official Java library plugin,
 * and links the declarative model to the official plugin.
 */
public abstract class StandaloneJavaLibraryPlugin implements Plugin<Project> {
    public static final String JAVA_LIBRARY = "javaLibrary";

    @SoftwareType(name = JAVA_LIBRARY, modelPublicType = JavaLibrary.class)
    abstract public JavaLibrary getLibrary();

    @Override
    public void apply(Project project) {
        JavaLibrary dslModel = getLibrary();
        project.getExtensions().add(JAVA_LIBRARY, dslModel);

        project.getPlugins().apply(JavaLibraryPlugin.class);

        project.getExtensions().getByType(TestingExtension.class).getSuites().withType(JvmTestSuite.class).named("test").configure(testSuite -> {
            testSuite.useJUnitJupiter();
        });

        linkDslModelToPlugin(project, dslModel);
    }

    @Inject
    protected abstract JavaToolchainService getJavaToolchainService();

    private void linkDslModelToPlugin(Project project, JavaLibrary dslModel) {
        JvmPluginSupport.linkJavaVersion(project, dslModel);
        JvmPluginSupport.linkMainSourceSourceSetDependencies(project, dslModel.getDependencies());
        JvmPluginSupport.linkTestJavaVersion(project, getJavaToolchainService(), dslModel.getTesting());
        JvmPluginSupport.linkTestSourceSourceSetDependencies(project, dslModel.getTesting().getDependencies());
    }
}
