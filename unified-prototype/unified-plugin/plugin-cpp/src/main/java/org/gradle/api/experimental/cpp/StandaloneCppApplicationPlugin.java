package org.gradle.api.experimental.cpp;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.language.cpp.plugins.CppApplicationPlugin;

public abstract class StandaloneCppApplicationPlugin implements Plugin<Project> {
    @SoftwareType(name = "cppApplication", modelPublicType = CppApplication.class)
    abstract public CppApplication getApplication();

    @Override
    public void apply(Project target) {
        target.getPlugins().apply(CppApplicationPlugin.class);
    }
}
