package org.gradle.api.experimental.swift;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.plugins.software.RegistersSoftwareTypes;

@RegistersSoftwareTypes({
        StandaloneSwiftLibraryPlugin.class,
        StandaloneSwiftApplicationPlugin.class})
public class SwiftEcosystemPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings target) {
    }
}
