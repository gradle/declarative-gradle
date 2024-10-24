package org.gradle.api.experimental.buildinit;

import org.gradle.buildinit.specs.BuildInitSpec;
import org.gradle.buildinit.specs.BuildInitGenerator;
import org.gradle.buildinit.specs.internal.BuildInitSpecRegistry;

import javax.inject.Inject;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public abstract class AbstractSpecContributingPlugin {
    protected abstract List<BuildInitSpec> getSpecs();

    protected Class<? extends BuildInitGenerator> getGenerator() {
        return StaticProjectGenerator.class;
    }

    protected void registerSpecs() {
        getBuildInitSpecRegistry().register(getGenerator(), getSpecs());
    }

    @Inject
    protected abstract BuildInitSpecRegistry getBuildInitSpecRegistry();
}
