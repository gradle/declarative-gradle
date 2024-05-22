package org.gradle.api.experimental.cpp;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.language.cpp.plugins.CppLibraryPlugin;

public abstract class StandaloneCppLibraryPlugin implements Plugin<Project> {
    @SoftwareType(name = "cppLibrary", modelPublicType = CppLibrary.class)
    abstract public CppLibrary getLibrary();

    @Override
    public void apply(Project target) {
        target.getPlugins().apply(CppLibraryPlugin.class);
    }
}
