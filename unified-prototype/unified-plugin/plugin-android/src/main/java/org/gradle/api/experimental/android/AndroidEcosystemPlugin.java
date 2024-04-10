package org.gradle.api.experimental.android;

import org.gradle.api.Plugin;
import org.gradle.api.experimental.android.application.StandaloneAndroidApplicationPlugin;
import org.gradle.api.experimental.android.library.StandaloneAndroidLibraryPlugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.SettingsInternal;
import org.gradle.plugin.software.internal.SoftwareTypeRegistry;

public class AndroidEcosystemPlugin implements Plugin<Settings> {

    @Override
    public void apply(Settings target) {
        // To be replaced with static annotations once https://github.com/gradle/gradle/pull/28736 is merged
        SettingsInternal settings = (SettingsInternal) target;
        SoftwareTypeRegistry softwareTypeRegistry = settings.getServices().get(SoftwareTypeRegistry.class);
        softwareTypeRegistry.register(StandaloneAndroidApplicationPlugin.class);
        softwareTypeRegistry.register(StandaloneAndroidLibraryPlugin.class);
    }
}
