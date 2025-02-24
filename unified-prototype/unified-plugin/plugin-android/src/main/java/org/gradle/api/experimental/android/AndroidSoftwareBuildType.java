package org.gradle.api.experimental.android;

import org.gradle.api.Action;
import org.gradle.api.experimental.android.extensions.BaselineProfile;
import org.gradle.api.experimental.android.extensions.Minify;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

import javax.inject.Inject;

public interface AndroidSoftwareBuildType {
    /**
     * Dependencies for this build type.
     */
    AndroidSoftwareDependencies getDependencies();

    @Nested
    Minify getMinify();

    @Configuring
    default void minify(Action<? super Minify> action) {
        action.execute(getMinify());
    }

    @Restricted
    ListProperty<ProguardFile> getProguardFiles();
    @Restricted
    ListProperty<ProguardFile> getDefaultProguardFiles();

    @Restricted
    default ProguardFile proguardFile(String name) {
        ProguardFile proguardFile = getObjectFactory().newInstance(ProguardFile.class);
        proguardFile.getName().set(name);
        return proguardFile;
    }

    @Inject
    ObjectFactory getObjectFactory();

    @Nested
    BaselineProfile getBaselineProfile();

    @Configuring
    default void baselineProfile(Action<? super BaselineProfile> action) {
        action.execute(getBaselineProfile());
    }
}
