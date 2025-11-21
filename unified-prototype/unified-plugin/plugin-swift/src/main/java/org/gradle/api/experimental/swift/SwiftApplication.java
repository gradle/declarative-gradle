package org.gradle.api.experimental.swift;

import org.gradle.api.experimental.common.HasApplicationDependencies;
import org.gradle.api.experimental.common.HasCliExecutables;
import org.gradle.api.internal.plugins.Definition;

public interface SwiftApplication extends HasSwiftTarget, HasApplicationDependencies, Definition<SwiftApplicationBuildModel> {
}
