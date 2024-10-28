package org.gradle.api.experimental.swift;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.experimental.common.CliApplicationConventionsPlugin;
import org.gradle.api.experimental.swift.internal.SwiftPluginSupport;
import org.gradle.api.file.RegularFile;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.swift.SwiftBinary;
import org.gradle.language.swift.SwiftComponent;
import org.gradle.language.swift.SwiftExecutable;
import org.gradle.language.swift.plugins.SwiftApplicationPlugin;
import org.gradle.util.internal.TextUtil;

public abstract class StandaloneSwiftApplicationPlugin implements Plugin<Project> {

    public static final String SWIFT_APPLICATION = "swiftApplication";

    @SoftwareType(name = SWIFT_APPLICATION, modelPublicType = SwiftApplication.class)
    public abstract SwiftApplication getApplication();

    @Override
    public void apply(Project project) {
        SwiftApplication application = getApplication();

        project.getPlugins().apply(SwiftApplicationPlugin.class);
        project.getPlugins().apply(CliApplicationConventionsPlugin.class);

        linkDslModelToPlugin(project, application);
    }

    private void linkDslModelToPlugin(Project project, SwiftApplication application) {
        SwiftComponent model = project.getExtensions().getByType(SwiftComponent.class);
        SwiftPluginSupport.linkSwiftVersion(application, model);

        model.getImplementationDependencies().getDependencies().addAllLater(application.getDependencies().getImplementation().getDependencies());

        project.afterEvaluate(p -> {
            for (SwiftBinary binary : model.getBinaries().get()) {
                if (binary instanceof SwiftExecutable) {
                    Provider<RegularFile> executable = ((SwiftExecutable) binary).getDebuggerExecutableFile();
                    TaskProvider<Exec> runTask = project.getTasks().register("run" + TextUtil.capitalize(binary.getName()), Exec.class, task -> {
                        task.executable(executable.get().getAsFile().getAbsoluteFile());
                        task.dependsOn(executable);
                    });
                    application.getRunTasks().add(runTask);
                }
            }
        });
    }
}
