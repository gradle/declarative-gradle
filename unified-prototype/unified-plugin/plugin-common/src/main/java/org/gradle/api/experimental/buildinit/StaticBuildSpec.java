package org.gradle.api.experimental.buildinit;

import org.gradle.buildinit.specs.BuildInitParameter;
import org.gradle.buildinit.specs.BuildInitSpec;

import java.util.Collections;
import java.util.List;

/**
 * An {@link BuildInitSpec} that represents a build that can be generated from a static template
 * using the {@link StaticBuildGenerator}.
 * <p>
 * The relative path to the root template directory from the {@code /templates`} directory in the
 * root of the resources dir (and classpath in the library jar) should be the same as the type.
 */
@SuppressWarnings("UnstableApiUsage")
public final class StaticBuildSpec implements BuildInitSpec {
    private final String type;
    private final String displayName;

    public StaticBuildSpec(String type, String displayName) {
        this.type = type;
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public List<BuildInitParameter<?>> getParameters() {
        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return type;
    }
}
