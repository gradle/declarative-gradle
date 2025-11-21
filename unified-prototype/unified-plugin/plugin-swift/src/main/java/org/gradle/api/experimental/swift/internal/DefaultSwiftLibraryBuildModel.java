package org.gradle.api.experimental.swift.internal;


import org.gradle.api.experimental.swift.SwiftLibraryBuildModel;
import org.gradle.language.swift.SwiftLibrary;

public class DefaultSwiftLibraryBuildModel implements SwiftLibraryBuildModel {
    private SwiftLibrary swiftLibrary;

    @Override
    public SwiftLibrary getSwiftLibrary() {
        return swiftLibrary;
    }

    public void setSwiftLibrary(SwiftLibrary swiftLibrary) {
        this.swiftLibrary = swiftLibrary;
    }
}
