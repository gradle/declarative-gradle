package org.gradle.api.experimental.swift;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.common.CliExecutablesSupport;
import org.gradle.api.experimental.swift.internal.DefaultSwiftApplicationBuildModel;
import org.gradle.api.experimental.swift.internal.SwiftPluginSupport;
import org.gradle.api.file.RegularFile;
import org.gradle.api.internal.plugins.BindsProjectType;
import org.gradle.api.internal.plugins.ProjectTypeBinding;
import org.gradle.api.internal.plugins.ProjectTypeBindingBuilder;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.swift.SwiftBinary;
import org.gradle.language.swift.SwiftComponent;
import org.gradle.language.swift.SwiftExecutable;
import org.gradle.language.swift.plugins.SwiftApplicationPlugin;
import org.gradle.util.internal.TextUtil;

@SuppressWarnings("UnstableApiUsage")
@BindsProjectType(StandaloneSwiftApplicationPlugin.Binding.class)
public abstract class StandaloneSwiftApplicationPlugin implements Plugin<Project> {

    public static final String SWIFT_APPLICATION = "swiftApplication";

    static class Binding implements ProjectTypeBinding {
        @Override
        public void bind(ProjectTypeBindingBuilder builder) {
            builder.bindProjectType(SWIFT_APPLICATION, SwiftApplication.class, (context, definition, buildModel) -> {
                context.getProject().getPlugins().apply(SwiftApplicationPlugin.class);
                CliExecutablesSupport.configureRunTasks(context.getProject().getTasks(), buildModel);

                ((DefaultSwiftApplicationBuildModel)buildModel).setSwiftComponent(context.getProject().getExtensions().getByType(SwiftComponent.class));

                linkDefinitionToPlugin(context.getProject(), definition, buildModel);
            })
            .withUnsafeDefinition()
            .withBuildModelImplementationType(DefaultSwiftApplicationBuildModel.class);
        }

        private void linkDefinitionToPlugin(Project project, SwiftApplication definition, SwiftApplicationBuildModel buildModel) {
            SwiftComponent model = buildModel.getSwiftComponent();
            SwiftPluginSupport.linkSwiftVersion(definition, model);

            model.getImplementationDependencies().getDependencies().addAllLater(definition.getDependencies().getImplementation().getDependencies());

            project.afterEvaluate(p -> {
                for (SwiftBinary binary : model.getBinaries().get()) {
                    if (binary instanceof SwiftExecutable) {
                        Provider<RegularFile> executable = ((SwiftExecutable) binary).getDebuggerExecutableFile();
                        TaskProvider<Exec> runTask = project.getTasks().register("run" + TextUtil.capitalize(binary.getName()), Exec.class, task -> {
                            task.executable(executable.get().getAsFile().getAbsoluteFile());
                            task.dependsOn(executable);
                        });
                        buildModel.getRunTasks().add(runTask);
                    }
                }
            });
        }
    }

    @Override
    public void apply(Project project) { }
}
