package org.gradle.api.experimental.swift;

import org.gradle.api.experimental.common.HasLibraryDependencies;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface SwiftLibrary extends HasSwiftTarget, HasLibraryDependencies {
}
