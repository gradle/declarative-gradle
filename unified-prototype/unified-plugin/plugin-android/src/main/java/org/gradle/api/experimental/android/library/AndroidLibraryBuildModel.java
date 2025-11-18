package org.gradle.api.experimental.android.library;

import com.android.build.api.dsl.LibraryExtension;
import org.gradle.api.internal.plugins.BuildModel;

public interface AndroidLibraryBuildModel extends BuildModel {
    LibraryExtension getLibraryExtension();
}
