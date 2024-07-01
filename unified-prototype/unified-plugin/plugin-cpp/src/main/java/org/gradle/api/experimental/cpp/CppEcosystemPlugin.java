package org.gradle.api.experimental.cpp;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.plugins.software.RegistersSoftwareTypes;

@RegistersSoftwareTypes({StandaloneCppLibraryPlugin.class, StandaloneCppApplicationPlugin.class})
public class CppEcosystemPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings target) {
    }
}
