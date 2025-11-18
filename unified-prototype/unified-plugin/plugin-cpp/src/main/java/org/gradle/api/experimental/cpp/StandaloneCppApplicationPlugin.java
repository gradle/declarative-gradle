package org.gradle.api.experimental.cpp;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.common.CliExecutablesSupport;
import org.gradle.api.experimental.cpp.internal.DefaultCppApplicationBuildModel;
import org.gradle.api.file.RegularFile;
import org.gradle.api.internal.plugins.BindsProjectType;
import org.gradle.api.internal.plugins.ProjectTypeBinding;
import org.gradle.api.internal.plugins.ProjectTypeBindingBuilder;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.cpp.CppComponent;
import org.gradle.language.cpp.CppExecutable;
import org.gradle.language.cpp.plugins.CppApplicationPlugin;
import org.gradle.util.internal.TextUtil;

@SuppressWarnings("UnstableApiUsage")
@BindsProjectType(StandaloneCppApplicationPlugin.Binding.class)
public abstract class StandaloneCppApplicationPlugin implements Plugin<Project> {
    public static final String CPP_APPLICATION = "cppApplication";

    static class Binding implements ProjectTypeBinding {
        @Override
        public void bind(ProjectTypeBindingBuilder builder) {
            builder.bindProjectType(CPP_APPLICATION, CppApplication.class, (context, definition, buildModel) ->{
                context.getProject().getPlugins().apply(CppApplicationPlugin.class);
                CliExecutablesSupport.configureRunTasks(context.getProject().getTasks(), buildModel);

                ((DefaultCppApplicationBuildModel) buildModel).setCppComponent(context.getProject().getExtensions().getByType(CppComponent.class));

                linkDefinitionToPlugin(context.getProject(), definition, buildModel);
            })
            .withBuildModelImplementationType(DefaultCppApplicationBuildModel.class);
        }

        private void linkDefinitionToPlugin(Project project, CppApplication definition, CppApplicationBuildModel buildModel) {
            CppComponent model = buildModel.getCppComponent();

            model.getImplementationDependencies().getDependencies().addAllLater(definition.getDependencies().getImplementation().getDependencies());

            project.getComponents().withType(org.gradle.language.cpp.CppApplication.class).configureEach(applicationComponent ->
                applicationComponent.getBinaries().configureEach(binary -> {
                    binary.getCompileTask().get().getCompilerArgs().add(definition.getCppVersion().map(v -> "--std=" + v));
                    if (binary instanceof CppExecutable) {
                        Provider<RegularFile> executable = ((CppExecutable) binary).getDebuggerExecutableFile();
                        TaskProvider<Exec> runTask = project.getTasks().register("run" + TextUtil.capitalize(binary.getName()), Exec.class, task -> {
                            task.executable(executable.get().getAsFile().getAbsoluteFile());
                            task.dependsOn(executable);
                        });
                        buildModel.getRunTasks().add(runTask);
                    }
                })
            );
        }
    }

    @Override
    public void apply(Project target) {
    }
}
