package org.gradle.api.experimental.cpp;

import org.gradle.api.experimental.common.HasApplicationDependencies;
import org.gradle.api.internal.plugins.Definition;

public interface CppApplication extends HasCppTarget, HasApplicationDependencies, Definition<CppApplicationBuildModel> {
}
