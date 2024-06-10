package org.gradle.api.experimental.android;

import org.gradle.api.Action;
import org.gradle.api.experimental.android.extensions.BaselineProfile;
import org.gradle.api.experimental.android.extensions.Minify;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Adding;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

import javax.inject.Inject;

@Restricted
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

    ListProperty<ProguardFile> getProguardFiles();
    ListProperty<ProguardFile> getDefaultProguardFiles();

    @Adding
    default ProguardFile proguardFile(Action<? super ProguardFile> configure) {
        ProguardFile proguardFile = getObjectFactory().newInstance(ProguardFile.class);
        proguardFile.getName().convention("<no name>");
        configure.execute(proguardFile);
        getProguardFiles().add(proguardFile);
        return proguardFile;
    }

    @Adding
    default ProguardFile defaultProguardFile(Action<? super ProguardFile> configure) {
        ProguardFile proguardFile = getObjectFactory().newInstance(ProguardFile.class);
        proguardFile.getName().convention("<no name>");
        configure.execute(proguardFile);
        getDefaultProguardFiles().add(proguardFile);
        return proguardFile;
    }

    @Inject
    ObjectFactory getObjectFactory();

    @Nested
    BaselineProfile getBaselineProfile();

    @Configuring
    default void baselineProfile(Action<? super BaselineProfile> action) {
        BaselineProfile baselineProfile = getBaselineProfile();
        baselineProfile.getEnabled().set(true);
        action.execute(baselineProfile);
    }
}
