package org.gradle.api.experimental.cpp;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.language.cpp.CppComponent;
import org.gradle.language.cpp.plugins.CppApplicationPlugin;

public abstract class StandaloneCppApplicationPlugin implements Plugin<Project> {
    @SoftwareType(name = "cppApplication", modelPublicType = CppApplication.class)
    abstract public CppApplication getApplication();

    @Override
    public void apply(Project target) {
        CppApplication application = getApplication();

        target.getPlugins().apply(CppApplicationPlugin.class);

        linkDslModelToPlugin(target, application);
    }

    private void linkDslModelToPlugin(Project project, CppApplication application) {
        CppComponent model = project.getExtensions().getByType(CppComponent.class);

        model.getImplementationDependencies().getDependencies().addAllLater(application.getDependencies().getImplementation().getDependencies());
    }
}
