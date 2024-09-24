package org.gradle.api.experimental.buildinit;

import org.gradle.buildinit.projectspecs.InitProjectParameter;
import org.gradle.buildinit.projectspecs.InitProjectSpec;

import java.util.Collections;
import java.util.List;

/**
 * An {@link InitProjectSpec} that represents a project that can be generated from a static template
 * using the {@link StaticProjectGenerator}.
 * <p>
 * The relative path to the root template directory from the {@code /templates`} directory in the
 * root of the resources dir (and classpath in the library jar) should be the same as the type.
 */
@SuppressWarnings("UnstableApiUsage")
public final class StaticProjectSpec implements InitProjectSpec {
    private final String type;
    private final String displayName;

    public StaticProjectSpec(String type, String displayName) {
        this.type = type;
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public List<InitProjectParameter<?>> getParameters() {
        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return type;
    }
}
