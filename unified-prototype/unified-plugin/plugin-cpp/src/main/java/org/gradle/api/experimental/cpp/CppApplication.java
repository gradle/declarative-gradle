package org.gradle.api.experimental.cpp;

import org.gradle.api.experimental.common.HasApplicationDependencies;
import org.gradle.api.experimental.common.HasCliExecutables;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface CppApplication extends HasCppTarget, HasApplicationDependencies, HasCliExecutables {
}
