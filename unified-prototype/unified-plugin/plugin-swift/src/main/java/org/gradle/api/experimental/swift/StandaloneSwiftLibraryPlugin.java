package org.gradle.api.experimental.swift;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.swift.internal.DefaultSwiftLibraryBuildModel;
import org.gradle.api.experimental.swift.internal.SwiftPluginSupport;
import org.gradle.api.internal.plugins.BindsProjectType;
import org.gradle.api.internal.plugins.ProjectTypeBinding;
import org.gradle.api.internal.plugins.ProjectTypeBindingBuilder;
import org.gradle.language.swift.plugins.SwiftLibraryPlugin;

@SuppressWarnings("UnstableApiUsage")
@BindsProjectType(StandaloneSwiftLibraryPlugin.Binding.class)
public abstract class StandaloneSwiftLibraryPlugin implements Plugin<Project> {

    public static final String SWIFT_LIBRARY = "swiftLibrary";

    static class Binding implements ProjectTypeBinding {
        @Override
        public void bind(ProjectTypeBindingBuilder builder) {
            builder.bindProjectType(SWIFT_LIBRARY, SwiftLibrary.class, (context, definition, buildModel) -> {
                context.getProject().getPlugins().apply(SwiftLibraryPlugin.class);

                ((DefaultSwiftLibraryBuildModel) buildModel).setSwiftLibrary(context.getProject().getExtensions().getByType(org.gradle.language.swift.SwiftLibrary.class));

                linkDefinitionToPlugin(definition, buildModel);
            })
            .withUnsafeDefinition()
            .withBuildModelImplementationType(DefaultSwiftLibraryBuildModel.class);
        }

        private void linkDefinitionToPlugin(SwiftLibrary definition, SwiftLibraryBuildModel buildModel) {
            org.gradle.language.swift.SwiftLibrary model = buildModel.getSwiftLibrary();
            SwiftPluginSupport.linkSwiftVersion(definition, model);

            model.getImplementationDependencies().getDependencies().addAllLater(definition.getDependencies().getImplementation().getDependencies());
            model.getApiDependencies().getDependencies().addAllLater(definition.getDependencies().getApi().getDependencies());
        }
    }

    @Override
    public void apply(Project project) { }
}
