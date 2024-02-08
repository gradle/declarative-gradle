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
public abstract class AndroidLibrary {
    private final AndroidTargets targets;

    public AndroidLibrary(AndroidTargets targets) {
        this.targets = targets;
    }

    /**
     * @see CommonExtension#getNamespace()
     */
    @Input
    public abstract Property<String> getNamespace();

    /**
     * @see CommonExtension#getCompileSdk()
     */
    @Input
    public abstract Property<Integer> getCompileSdk();

    /**
     * @see BaseFlavor#getMinSdk()
     */
    @Input
    public abstract Property<Integer> getMinSdk();

    /**
     * JDK version to use for compilation.
     */
    @Input
    public abstract Property<Integer> getJdkVersion();

    /**
     * Common dependencies for all targets.
     */
    @Nested
    public abstract LibraryDependencies getDependencies();

    public void dependencies(Action<? super LibraryDependencies> action) {
        action.execute(getDependencies());
    }

    /**
     * Static targets extension block.
     */
    @Nested
    public AndroidTargets getTargets() {
        return targets;
    }

    public void targets(Action<? super AndroidTargets> action) {
        action.execute(getTargets());
    }
}
