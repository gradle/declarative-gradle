package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.experimental.buildinit.StaticBuildGenerator;
import org.gradle.api.experimental.buildinit.StaticBuildSpec;
import org.gradle.api.initialization.Settings;
import org.gradle.buildinit.specs.internal.BuildInitSpecRegistry;

import javax.inject.Inject;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public abstract class JvmEcosystemInitPlugin implements Plugin<Settings> {
    @Inject
    protected abstract BuildInitSpecRegistry getBuildInitSpecRegistry();

    @Override
    public void apply(Settings settings) {
        getBuildInitSpecRegistry().register(StaticBuildGenerator.class, List.of(
                new StaticBuildSpec("java-application", "Declarative Java Application Project")
        ));
    }
}
