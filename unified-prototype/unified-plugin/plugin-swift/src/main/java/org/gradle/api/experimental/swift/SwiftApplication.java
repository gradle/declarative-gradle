package org.gradle.api.experimental.swift;

import org.gradle.api.experimental.common.HasApplicationDependencies;
import org.gradle.api.experimental.common.HasCliExecutables;

public interface SwiftApplication extends HasSwiftTarget, HasApplicationDependencies, HasCliExecutables {
}
