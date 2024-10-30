package org.gradle.api.experimental.kmp;

import org.gradle.api.Plugin;
import org.gradle.api.experimental.buildinit.StaticProjectGenerator;
import org.gradle.api.experimental.buildinit.StaticProjectSpec;
import org.gradle.api.experimental.jvm.JvmEcosystemConventionsPlugin;
import org.gradle.api.experimental.kotlin.StandaloneKotlinJvmApplicationPlugin;
import org.gradle.api.experimental.kotlin.StandaloneKotlinJvmLibraryPlugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.plugins.software.RegistersSoftwareTypes;
import org.gradle.buildinit.specs.internal.BuildInitSpecRegistry;

import javax.inject.Inject;
import java.util.List;


@SuppressWarnings("UnstableApiUsage")
@RegistersSoftwareTypes({
        StandaloneKmpLibraryPlugin.class,
        StandaloneKmpApplicationPlugin.class,
        StandaloneKotlinJvmLibraryPlugin.class,
        StandaloneKotlinJvmApplicationPlugin.class})
public abstract class KmpEcosystemPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings target) {
        getBuildInitSpecRegistry().register(StaticProjectGenerator.class, List.of(
                new StaticProjectSpec("kotlin-application", "Declarative Kotlin (JVM) Application Project")
        ));
        target.getPlugins().apply(JvmEcosystemConventionsPlugin.class);
    }

    @Inject
    protected abstract BuildInitSpecRegistry getBuildInitSpecRegistry();
}
