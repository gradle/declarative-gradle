package org.gradle.api.experimental.buildinit;

import org.gradle.buildinit.projectspecs.InitProjectParameter;
import org.gradle.buildinit.projectspecs.InitProjectSpec;

import java.util.Collections;
import java.util.List;

/**
 * An {@link InitProjectSpec} that represents a project that can be generated from a static template
 * using the {@link StaticProjectGenerator}
 */
@SuppressWarnings("UnstableApiUsage")
public final class StaticProjectSpec {}
//implements InitProjectSpec {
//    private final String templatePath;
//    private final String displayName;
//
//    public StaticProjectSpec(String templatePath, String displayName) {
//        this.templatePath = templatePath;
//        this.displayName = displayName;
//    }
//
//    @Override
//    public String getDisplayName() {
//        return displayName;
//    }
//
//    @Override
//    public List<InitProjectParameter<?>> getParameters() {
//        return Collections.emptyList();
//    }
//
//    public String getTemplatePath() {
//        return templatePath;
//    }
//}
