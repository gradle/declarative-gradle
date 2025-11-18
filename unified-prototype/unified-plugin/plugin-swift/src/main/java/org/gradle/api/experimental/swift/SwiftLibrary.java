package org.gradle.api.experimental.swift;

import org.gradle.api.experimental.common.HasLibraryDependencies;
import org.gradle.api.internal.plugins.Definition;

public interface SwiftLibrary extends HasSwiftTarget, HasLibraryDependencies, Definition<SwiftLibraryBuildModel> {
}
