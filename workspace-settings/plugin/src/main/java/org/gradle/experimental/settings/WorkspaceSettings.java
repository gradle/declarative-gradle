package org.gradle.experimental.settings;

import org.gradle.api.Action;

public interface WorkspaceSettings {
    RootBuildSpecification build(Action<? super RootBuildSpecification> action);
}
