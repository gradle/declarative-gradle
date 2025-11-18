package org.gradle.api.experimental.swift;

import org.gradle.api.experimental.common.HasCliExecutables;
import org.gradle.api.internal.plugins.BuildModel;
import org.gradle.language.swift.SwiftComponent;

public interface SwiftApplicationBuildModel extends BuildModel, HasCliExecutables {
    SwiftComponent getSwiftComponent();
}
