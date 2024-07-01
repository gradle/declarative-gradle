package org.gradle.api.experimental.cpp;

import org.gradle.api.experimental.common.HasLibraryDependencies;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface CppLibrary extends HasCppTarget, HasLibraryDependencies {
}
