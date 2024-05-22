package org.gradle.api.experimental.common;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CliApplicationConventionsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        HasCliExecutables executables = target.getExtensions().getByType(HasCliExecutables.class);
        target.getTasks().register("runAll", task -> task.dependsOn(executables.getRunTasks()));
    }
}
