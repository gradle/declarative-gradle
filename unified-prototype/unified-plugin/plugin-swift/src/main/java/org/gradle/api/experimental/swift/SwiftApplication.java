package org.gradle.api.experimental.swift;

import org.gradle.api.experimental.common.HasApplicationDependencies;
import org.gradle.api.experimental.common.HasCliExecutables;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface SwiftApplication extends HasSwiftTarget, HasApplicationDependencies, HasCliExecutables {
}
