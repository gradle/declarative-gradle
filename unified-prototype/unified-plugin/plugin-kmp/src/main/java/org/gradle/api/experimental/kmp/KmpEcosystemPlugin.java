package org.gradle.api.experimental.kmp;

import org.gradle.api.Plugin;
import org.gradle.api.experimental.buildinit.AbstractSpecContributingPlugin;
import org.gradle.api.experimental.buildinit.StaticProjectSpec;
import org.gradle.api.experimental.jvm.JvmEcosystemConventionsPlugin;
import org.gradle.api.experimental.kotlin.StandaloneKotlinJvmApplicationPlugin;
import org.gradle.api.experimental.kotlin.StandaloneKotlinJvmLibraryPlugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.plugins.software.RegistersSoftwareTypes;
import org.gradle.buildinit.specs.BuildInitSpec;

import java.util.List;


@SuppressWarnings("UnstableApiUsage")
@RegistersSoftwareTypes({
        StandaloneKmpLibraryPlugin.class,
        StandaloneKmpApplicationPlugin.class,
        StandaloneKotlinJvmLibraryPlugin.class,
        StandaloneKotlinJvmApplicationPlugin.class})
public abstract class KmpEcosystemPlugin extends AbstractSpecContributingPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings target) {
        registerSpecs();
        target.getPlugins().apply(JvmEcosystemConventionsPlugin.class);
    }

    @Override
    protected List<BuildInitSpec> getSpecs() {
        return List.of(
            new StaticProjectSpec("kotlin-application", "Declarative Kotlin (JVM) Application Project")
        );
    }
}
