package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.experimental.buildinit.StaticProjectGenerator;
import org.gradle.api.experimental.buildinit.StaticProjectSpec;
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
        getBuildInitSpecRegistry().register(StaticProjectGenerator.class, List.of(
                new StaticProjectSpec("java-application", "Declarative Java Application Project")
        ));
    }
}
