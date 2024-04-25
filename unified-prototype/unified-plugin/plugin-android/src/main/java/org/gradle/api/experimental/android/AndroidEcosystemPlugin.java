package org.gradle.api.experimental.android;

import org.gradle.api.Plugin;
import org.gradle.api.experimental.android.application.StandaloneAndroidApplicationPlugin;
import org.gradle.api.experimental.android.library.StandaloneAndroidLibraryPlugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.SettingsInternal;
import org.gradle.api.internal.plugins.software.RegistersSoftwareTypes;
import org.gradle.plugin.software.internal.SoftwareTypeRegistry;

@SuppressWarnings("UnstableApiUsage")
@RegistersSoftwareTypes({StandaloneAndroidApplicationPlugin.class, StandaloneAndroidLibraryPlugin.class})
public class AndroidEcosystemPlugin implements Plugin<Settings> {

    @Override
    public void apply(Settings target) { }
}
