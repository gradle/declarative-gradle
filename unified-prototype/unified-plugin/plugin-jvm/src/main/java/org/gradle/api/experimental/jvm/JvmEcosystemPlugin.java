package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.experimental.java.StandaloneJavaApplicationPlugin;
import org.gradle.api.experimental.java.StandaloneJavaLibraryPlugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.SettingsInternal;
import org.gradle.api.internal.plugins.software.RegistersSoftwareTypes;
import org.gradle.plugin.software.internal.SoftwareTypeRegistry;

@RegistersSoftwareTypes({
        StandaloneJavaApplicationPlugin.class,
        StandaloneJavaLibraryPlugin.class,
        StandaloneJvmLibraryPlugin.class,
        StandaloneJvmApplicationPlugin.class
})
public class JvmEcosystemPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings target) { }
}
