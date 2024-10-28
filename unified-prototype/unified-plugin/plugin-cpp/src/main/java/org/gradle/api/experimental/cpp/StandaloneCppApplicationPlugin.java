package org.gradle.api.experimental.cpp;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.common.CliApplicationConventionsPlugin;
import org.gradle.api.file.RegularFile;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.CppComponent;
import org.gradle.language.cpp.CppExecutable;
import org.gradle.language.cpp.plugins.CppApplicationPlugin;
import org.gradle.util.internal.TextUtil;

public abstract class StandaloneCppApplicationPlugin implements Plugin<Project> {
    public static final String CPP_APPLICATION = "cppApplication";

    @SoftwareType(name = CPP_APPLICATION)
    public abstract CppApplication getApplication();

    @Override
    public void apply(Project target) {
        CppApplication application = getApplication();

        target.getPlugins().apply(CppApplicationPlugin.class);
        target.getPlugins().apply(CliApplicationConventionsPlugin.class);

        linkDslModelToPlugin(target, application);
    }

    private void linkDslModelToPlugin(Project project, CppApplication application) {
        CppComponent model = project.getExtensions().getByType(CppComponent.class);

        model.getImplementationDependencies().getDependencies().addAllLater(application.getDependencies().getImplementation().getDependencies());

        project.getComponents().withType(org.gradle.language.cpp.CppApplication.class).configureEach(applicationComponent ->
            applicationComponent.getBinaries().configureEach(binary -> {
                binary.getCompileTask().get().getCompilerArgs().add(application.getCppVersion().map(v -> "--std=" + v));
                if (binary instanceof CppExecutable) {
                    Provider<RegularFile> executable = ((CppExecutable) binary).getDebuggerExecutableFile();
                    TaskProvider<Exec> runTask = project.getTasks().register("run" + TextUtil.capitalize(binary.getName()), Exec.class, task -> {
                        task.executable(executable.get().getAsFile().getAbsoluteFile());
                        task.dependsOn(executable);
                    });
                    application.getRunTasks().add(runTask);
                }
            })
        );
    }
}
