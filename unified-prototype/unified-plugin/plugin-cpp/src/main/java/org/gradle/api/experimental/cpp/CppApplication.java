package org.gradle.api.experimental.cpp;

import org.gradle.api.experimental.common.HasApplicationDependencies;
import org.gradle.api.experimental.common.HasCliExecutables;

public interface CppApplication extends HasCppTarget, HasApplicationDependencies, HasCliExecutables {
}
