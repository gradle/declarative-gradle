package org.gradle.api.experimental.common;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CliApplicationConventionsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        HasCliExecutables executables = project.getExtensions().create("HasCliExecutables", HasCliExecutables.class); // TODO: Is this meant to be automatically created?
        project.getTasks().register("runAll", task -> task.dependsOn(executables.getRunTasks()));
    }
}
