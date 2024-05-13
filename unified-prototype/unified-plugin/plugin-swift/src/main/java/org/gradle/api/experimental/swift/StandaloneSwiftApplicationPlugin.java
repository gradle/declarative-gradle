package org.gradle.api.experimental.swift;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.swift.internal.SwiftPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.language.swift.SwiftComponent;
import org.gradle.language.swift.plugins.SwiftApplicationPlugin;

public abstract class StandaloneSwiftApplicationPlugin implements Plugin<Project> {
    @SoftwareType(name = "swiftApplication", modelPublicType = SwiftApplication.class)
    abstract public SwiftApplication getApplication();

    @Override
    public void apply(Project project) {
        SwiftApplication application = getApplication();

        project.getPlugins().apply(SwiftApplicationPlugin.class);

        linkDslModelToPlugin(project, application);
    }

    private void linkDslModelToPlugin(Project project, SwiftApplication application) {
        SwiftComponent model = project.getExtensions().getByType(SwiftComponent.class);
        SwiftPluginSupport.linkSwiftVersion(application, model);
    }
}
