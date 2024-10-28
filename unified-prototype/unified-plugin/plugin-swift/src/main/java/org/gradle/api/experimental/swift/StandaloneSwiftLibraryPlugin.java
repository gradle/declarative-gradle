package org.gradle.api.experimental.swift;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.swift.internal.SwiftPluginSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.language.swift.plugins.SwiftLibraryPlugin;

public abstract class StandaloneSwiftLibraryPlugin implements Plugin<Project> {

    public static final String SWIFT_LIBRARY = "swiftLibrary";

    @SoftwareType(name = SWIFT_LIBRARY, modelPublicType = SwiftLibrary.class)
    public abstract SwiftLibrary getLibrary();

    @Override
    public void apply(Project project) {
        SwiftLibrary library = getLibrary();

        project.getPlugins().apply(SwiftLibraryPlugin.class);

        linkDslModelToPlugin(project, library);
    }

    private void linkDslModelToPlugin(Project project, SwiftLibrary library) {
        org.gradle.language.swift.SwiftLibrary model = project.getExtensions().getByType(org.gradle.language.swift.SwiftLibrary.class);
        SwiftPluginSupport.linkSwiftVersion(library, model);

        model.getImplementationDependencies().getDependencies().addAllLater(library.getDependencies().getImplementation().getDependencies());
        model.getApiDependencies().getDependencies().addAllLater(library.getDependencies().getApi().getDependencies());
    }
}
