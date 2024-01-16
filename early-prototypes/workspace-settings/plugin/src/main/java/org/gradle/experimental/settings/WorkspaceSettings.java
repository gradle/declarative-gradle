package org.gradle.experimental.settings;

import org.gradle.api.Action;
import org.gradle.api.initialization.Settings;

public interface WorkspaceSettings {
    RootProjectSpecification layout(Action<? super RootProjectSpecification> action);

    void build(Action<? super BuildSpecification> action);
}
