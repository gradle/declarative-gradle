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
                new StaticProjectSpec("android-application", "Android application with Android libraries"),
                new StaticProjectSpec("android-application-basic-activity", "Android Application with a basic Activity"),
                new StaticProjectSpec("android-application-empty-activity", "Android Application with an empty Activity")
        ));
    }

    @Inject
    protected abstract BuildInitSpecRegistry getBuildInitSpecRegistry();
}
