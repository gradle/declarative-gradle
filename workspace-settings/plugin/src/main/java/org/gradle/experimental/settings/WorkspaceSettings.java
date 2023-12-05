package org.gradle.experimental.settings;

import org.gradle.api.Action;
import org.gradle.api.initialization.Settings;

public interface WorkspaceSettings {
    RootBuildSpecification projects(Action<? super RootBuildSpecification> action);

    void build(String name, Action<? super Settings> action);
}
