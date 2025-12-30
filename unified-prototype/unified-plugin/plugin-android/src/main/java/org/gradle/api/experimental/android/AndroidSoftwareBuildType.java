package org.gradle.api.experimental.android;

import org.gradle.api.experimental.android.extensions.BaselineProfile;
import org.gradle.api.experimental.android.extensions.Minify;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.HiddenInDefinition;

import javax.inject.Inject;

public interface AndroidSoftwareBuildType {
    /**
     * Dependencies for this build type.
     */
    AndroidSoftwareDependencies getDependencies();

    @Nested
    Minify getMinify();

    ListProperty<ProguardFile> getProguardFiles();

    ListProperty<ProguardFile> getDefaultProguardFiles();

    default ProguardFile proguardFile(String name) {
        ProguardFile proguardFile = getObjectFactory().newInstance(ProguardFile.class);
        proguardFile.getName().set(name);
        return proguardFile;
    }

    @Inject
    @HiddenInDefinition
    ObjectFactory getObjectFactory();

    @Nested
    BaselineProfile getBaselineProfile();
}
