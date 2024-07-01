package org.gradle.api.experimental.java;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.jvm.internal.JvmPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.plugins.JavaLibraryPlugin;

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

        linkDslModelToPlugin(project, dslModel);
    }

    private void linkDslModelToPlugin(Project project, JavaLibrary dslModel) {
        JvmPluginSupport.linkJavaVersion(project, dslModel);
        JvmPluginSupport.linkMainSourceSourceSetDependencies(project, dslModel.getDependencies());
    }
}
