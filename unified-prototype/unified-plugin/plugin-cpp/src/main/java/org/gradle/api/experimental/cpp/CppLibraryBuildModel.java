package org.gradle.api.experimental.cpp;

import org.gradle.api.internal.plugins.BuildModel;
import org.gradle.language.cpp.CppLibrary;

public interface CppLibraryBuildModel extends BuildModel {
    CppLibrary getCppLibrary();
}
