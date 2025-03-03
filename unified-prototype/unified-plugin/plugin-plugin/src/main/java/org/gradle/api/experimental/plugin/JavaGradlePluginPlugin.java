package org.gradle.api.experimental.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.plugins.software.SoftwareType;

/**
 * Creates a declarative {@link JavaGradlePlugin} DSL model, applies the Gradle java-gradle-plugin plugin,
 * and links the declarative definition to the Gradle plugin.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class JavaGradlePluginPlugin implements Plugin<Project> {
    public static final String JAVA_GRADLE_PLUGIN = "javaGradlePlugin";

    @SoftwareType(name = JAVA_GRADLE_PLUGIN, modelPublicType = JavaGradlePlugin.class)
    public abstract JavaGradlePlugin getGradlePlugin();

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("java-gradle-plugin");
    }
}
