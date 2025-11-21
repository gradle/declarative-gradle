package org.gradle.api.experimental.cpp.internal;

import org.gradle.api.experimental.cpp.CppApplicationBuildModel;
import org.gradle.language.cpp.CppComponent;

abstract public class DefaultCppApplicationBuildModel implements CppApplicationBuildModel {
    private CppComponent cppComponent;

    @Override
    public CppComponent getCppComponent() {
        return cppComponent;
    }

    public void setCppComponent(CppComponent cppComponent) {
        this.cppComponent = cppComponent;
    }
}
