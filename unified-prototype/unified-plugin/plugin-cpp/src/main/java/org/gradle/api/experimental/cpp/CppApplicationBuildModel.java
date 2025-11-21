package org.gradle.api.experimental.cpp;

import org.gradle.api.experimental.common.HasCliExecutables;
import org.gradle.api.internal.plugins.BuildModel;
import org.gradle.language.cpp.CppComponent;

public interface CppApplicationBuildModel extends HasCliExecutables, BuildModel {
    CppComponent getCppComponent();
}
