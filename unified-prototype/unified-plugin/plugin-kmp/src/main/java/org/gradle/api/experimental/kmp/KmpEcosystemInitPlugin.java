package org.gradle.api.experimental.kmp;

import org.gradle.api.Plugin;
import org.gradle.api.experimental.buildinit.StaticBuildGenerator;
import org.gradle.api.experimental.buildinit.StaticBuildSpec;
import org.gradle.api.initialization.Settings;
import org.gradle.buildinit.specs.internal.BuildInitSpecRegistry;

import javax.inject.Inject;
import java.util.List;


@SuppressWarnings("UnstableApiUsage")
public abstract class KmpEcosystemInitPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings settings) {
        getBuildInitSpecRegistry().register(StaticBuildGenerator.class, List.of(
                new StaticBuildSpec("kotlin-application", "Declarative Kotlin (JVM) Application Project")
        ));
    }

    @Inject
    protected abstract BuildInitSpecRegistry getBuildInitSpecRegistry();
}
