package org.gradle.api.experimental.swift.internal;

import org.gradle.api.experimental.swift.HasSwiftTarget;
import org.gradle.language.swift.SwiftComponent;
import org.gradle.language.swift.SwiftVersion;

public class SwiftPluginSupport {
    public static void linkSwiftVersion(HasSwiftTarget component, SwiftComponent model) {
        model.getSourceCompatibility().set(component.getSwiftVersion().map(m -> {
            if (m == 3) {
                return SwiftVersion.SWIFT3;
            } else if (m == 4) {
                return SwiftVersion.SWIFT4;
            } else if (m == 5) {
                return SwiftVersion.SWIFT5;
            } else {
                throw new IllegalArgumentException("Unsupported Swift version " + m);
            }
        }));
    }
}
