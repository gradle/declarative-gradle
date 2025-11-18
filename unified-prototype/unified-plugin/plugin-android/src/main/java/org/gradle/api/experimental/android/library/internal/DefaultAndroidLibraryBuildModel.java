package org.gradle.api.experimental.android.library.internal;

import com.android.build.api.dsl.LibraryExtension;
import org.gradle.api.experimental.android.library.AndroidLibraryBuildModel;

public class DefaultAndroidLibraryBuildModel implements AndroidLibraryBuildModel {
    private LibraryExtension extension;

    public LibraryExtension getLibraryExtension() {
        return extension;
    }

    public void setLibraryExtension(LibraryExtension extension) {
        this.extension = extension;
    }
}
