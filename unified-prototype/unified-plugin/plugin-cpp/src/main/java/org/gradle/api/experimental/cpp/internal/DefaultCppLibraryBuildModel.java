package org.gradle.api.experimental.cpp.internal;

import org.gradle.api.experimental.cpp.CppLibraryBuildModel;
import org.gradle.language.cpp.CppLibrary;

public class DefaultCppLibraryBuildModel implements CppLibraryBuildModel {
    private CppLibrary cppLibrary;

    @Override
    public CppLibrary getCppLibrary() {
        return cppLibrary;
    }

    public void setCppLibrary(CppLibrary cppLibrary) {
        this.cppLibrary = cppLibrary;
    }
}
