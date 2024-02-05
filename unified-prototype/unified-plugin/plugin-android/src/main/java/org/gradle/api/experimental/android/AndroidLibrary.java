package org.gradle.api.experimental.android;

import com.android.build.api.dsl.BaseFlavor;
import com.android.build.api.dsl.CommonExtension;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;

/**
 * The public DSL interface for a declarative Android library.
 */
public interface AndroidLibrary {

    /**
     * @see CommonExtension#getNamespace()
     */
    @Input
    Property<String> getNamespace();

    /**
     * @see CommonExtension#getCompileSdk()
     */
    @Input
    Property<Integer> getCompileSdk();

    /**
     * @see BaseFlavor#getMinSdk()
     */
    @Input
    Property<Integer> getMinSdk();

    /**
     * JDK version to use for compilation.
     */
    @Input
    Property<Integer> getJdkVersion();

    /**
     * Common dependencies for all targets.
     */
    @Nested
    LibraryDependencies getDependencies();

    default void dependencies(Action<? super LibraryDependencies> action) {
        action.execute(getDependencies());
    }

    @Nested
    NamedDomainObjectContainer<AndroidTarget> getTargets();

    default void targets(Action<? super NamedDomainObjectContainer<AndroidTarget>> action) {
        action.execute(getTargets());
    }

}
