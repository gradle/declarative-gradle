package org.gradle.api.experimental;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.android.internal.AndroidLibraryAccessor;
import org.gradle.api.experimental.jvm.internal.JvmLibraryAccessor;
import org.gradle.api.experimental.kmp.internal.KmpLibraryAccessor;

/**
 * Responsible for registering the Android, JVM, and KMP declarative DSL
 * accessors with the Project.
 */
public class UnifiedPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().add("jvmLibrary", JvmLibraryAccessor.class);
        project.getExtensions().add("androidLibrary", AndroidLibraryAccessor.class);
        project.getExtensions().add("kmpLibrary", KmpLibraryAccessor.class);
    }
}