package org.gradle.api.experimental.common;

import org.gradle.api.Task;
import org.gradle.api.provider.ListProperty;

public interface HasCliExecutables {
    ListProperty<Task> getRunTasks();
}
