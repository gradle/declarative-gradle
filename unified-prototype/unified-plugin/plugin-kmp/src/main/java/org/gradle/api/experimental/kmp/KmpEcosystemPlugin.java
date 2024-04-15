package org.gradle.api.experimental.kmp;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.SettingsInternal;
import org.gradle.api.internal.plugins.software.RegistersSoftwareTypes;
import org.gradle.plugin.software.internal.SoftwareTypeRegistry;


@RegistersSoftwareTypes(StandaloneKmpLibraryPlugin.class)
public class KmpEcosystemPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings target) { }
}
