package org.gradle.api.experimental.swift;

import org.gradle.api.internal.plugins.BuildModel;

public interface SwiftLibraryBuildModel extends BuildModel {
    org.gradle.language.swift.SwiftLibrary getSwiftLibrary();
}
