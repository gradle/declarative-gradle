package org.gradle.api.experimental.swift;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.common.CliExecutablesSupport;
import org.gradle.api.experimental.swift.internal.DefaultSwiftApplicationBuildModel;
import org.gradle.api.experimental.swift.internal.SwiftPluginSupport;
import org.gradle.api.file.RegularFile;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.features.annotations.BindsProjectType;
import org.gradle.features.binding.ProjectFeatureApplicationContext;
import org.gradle.features.binding.ProjectTypeApplyAction;
import org.gradle.features.binding.ProjectTypeBinding;
import org.gradle.features.binding.ProjectTypeBindingBuilder;
import org.gradle.features.registration.TaskRegistrar;
import org.gradle.language.swift.SwiftBinary;
import org.gradle.language.swift.SwiftComponent;
import org.gradle.language.swift.SwiftExecutable;
import org.gradle.language.swift.plugins.SwiftApplicationPlugin;
import org.gradle.util.internal.TextUtil;

import javax.inject.Inject;

@SuppressWarnings("UnstableApiUsage")
@BindsProjectType(StandaloneSwiftApplicationPlugin.Binding.class)
public abstract class StandaloneSwiftApplicationPlugin implements Plugin<Project> {

    public static final String SWIFT_APPLICATION = "swiftApplication";

    static class Binding implements ProjectTypeBinding {
        @Override
        public void bind(ProjectTypeBindingBuilder builder) {
            builder.bindProjectType(SWIFT_APPLICATION, SwiftApplication.class, ApplyAction.class)
                .withUnsafeDefinition()
                .withUnsafeApplyAction()
                .withBuildModelImplementationType(DefaultSwiftApplicationBuildModel.class);
        }

        @SuppressWarnings("UnstableApiUsage")
        static abstract class ApplyAction implements ProjectTypeApplyAction<SwiftApplication, SwiftApplicationBuildModel> {
            @Inject
            public ApplyAction() {
            }

            @Inject
            protected abstract PluginManager getPluginManager();

            @Inject
            protected abstract TaskRegistrar getTaskRegistrar();

            @Inject
            protected abstract Project getProject();

            @Override
            public void apply(ProjectFeatureApplicationContext context, SwiftApplication definition, SwiftApplicationBuildModel buildModel) {
                getPluginManager().apply(SwiftApplicationPlugin.class);
                CliExecutablesSupport.configureRunTasks(getTaskRegistrar(), buildModel);

                ((DefaultSwiftApplicationBuildModel) buildModel).setSwiftComponent(getProject().getExtensions().getByType(SwiftComponent.class));

                linkDefinitionToPlugin(getProject(), definition, buildModel);
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
    }

    @Override
    public void apply(Project project) { }
}
