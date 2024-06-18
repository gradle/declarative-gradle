package org.gradle.api.experimental.kmp;

import org.gradle.api.Plugin;
import org.gradle.api.experimental.jvm.JvmEcosystemConventionsPlugin;
import org.gradle.api.experimental.kotlin.StandaloneKotlinJvmApplicationPlugin;
import org.gradle.api.experimental.kotlin.StandaloneKotlinJvmLibraryPlugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.plugins.software.RegistersSoftwareTypes;


@RegistersSoftwareTypes({
        StandaloneKmpLibraryPlugin.class,
        StandaloneKmpApplicationPlugin.class,
        StandaloneKotlinJvmLibraryPlugin.class,
        StandaloneKotlinJvmApplicationPlugin.class})
public class KmpEcosystemPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings target) {
        target.getPlugins().apply(JvmEcosystemConventionsPlugin.class);
    }
}
