package org.gradle.api.experimental.common;

import org.gradle.api.tasks.TaskContainer;

public class CliExecutablesSupport {
    public static void configureRunTasks(TaskContainer tasks, HasCliExecutables executables) {
        tasks.register("runAll", task -> task.dependsOn(executables.getRunTasks()));
    }
}
