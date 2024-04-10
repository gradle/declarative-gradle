package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.experimental.java.StandaloneJavaApplicationPlugin;
import org.gradle.api.experimental.java.StandaloneJavaLibraryPlugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.SettingsInternal;
import org.gradle.plugin.software.internal.SoftwareTypeRegistry;

public class JvmEcosystemPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings target) {
        // To be replaced with static annotations once https://github.com/gradle/gradle/pull/28736 is merged
        SettingsInternal settings = (SettingsInternal) target;
        SoftwareTypeRegistry softwareTypeRegistry = settings.getServices().get(SoftwareTypeRegistry.class);
        softwareTypeRegistry.register(StandaloneJavaApplicationPlugin.class);
        softwareTypeRegistry.register(StandaloneJavaLibraryPlugin.class);
        softwareTypeRegistry.register(StandaloneJvmLibraryPlugin.class);
    }
}
