package org.gradle.api.experimental.android;

import org.gradle.api.Plugin;
import org.gradle.api.experimental.buildinit.StaticProjectGenerator;
import org.gradle.api.experimental.buildinit.StaticProjectSpec;
import org.gradle.api.initialization.Settings;
import org.gradle.buildinit.specs.internal.BuildInitSpecRegistry;

import javax.inject.Inject;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public abstract class AndroidEcosystemInitPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings settings) {
        // TODO: Add something for generating AGP-based templates
        getBuildInitSpecRegistry().register(StaticProjectGenerator.class, List.of(
                new StaticProjectSpec("android-application", "Declarative Android Application Project"),
                new StaticProjectSpec("android-application-basic-activity", "Declarative Android Application Project with Basic Activity"),
                new StaticProjectSpec("android-application-empty-activity", "Declarative Android Application Project with Empty Activity")
        ));
    }

    @Inject
    protected abstract BuildInitSpecRegistry getBuildInitSpecRegistry();
}
