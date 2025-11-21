package org.gradle.api.experimental.swift.internal;

import org.gradle.api.experimental.swift.SwiftApplicationBuildModel;
import org.gradle.language.swift.SwiftComponent;

abstract public class DefaultSwiftApplicationBuildModel implements SwiftApplicationBuildModel {
    private SwiftComponent swiftComponent;

    @Override
    public SwiftComponent getSwiftComponent() {
        return swiftComponent;
    }

    public void setSwiftComponent(SwiftComponent swiftComponent) {
        this.swiftComponent = swiftComponent;
    }
}
