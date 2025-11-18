package org.gradle.api.experimental.cpp;

import org.gradle.api.experimental.common.HasLibraryDependencies;
import org.gradle.api.internal.plugins.Definition;

public interface CppLibrary extends HasCppTarget, HasLibraryDependencies, Definition<CppLibraryBuildModel> {
}
