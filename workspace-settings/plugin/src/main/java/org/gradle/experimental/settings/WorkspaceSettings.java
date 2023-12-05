package org.gradle.experimental.settings;

import org.gradle.api.Action;

public interface WorkspaceSettings {
    RootBuildSpecification projects(Action<? super RootBuildSpecification> action);
}
