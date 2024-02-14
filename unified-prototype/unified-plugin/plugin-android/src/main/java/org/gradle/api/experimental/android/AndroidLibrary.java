package org.gradle.api.experimental.android;

import com.android.build.api.dsl.BaseFlavor;
import com.android.build.api.dsl.CommonExtension;
import com.h0tk3y.kotlin.staticObjectNotation.Configuring;
import com.h0tk3y.kotlin.staticObjectNotation.Restricted;
import org.gradle.api.Action;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

/**
 * The public DSL interface for a declarative Android library.
 */
@Restricted
public abstract class AndroidLibrary {
    private final LibraryDependencies dependencies;
    private final AndroidTargets targets;

    @Inject
    public AndroidLibrary(AndroidTargets targets, ObjectFactory objectFactory) {
        this.targets = targets;
        this.dependencies = objectFactory.newInstance(LibraryDependencies.class);
    }

    /**
     * @see CommonExtension#getNamespace()
     */
    @Restricted
    public abstract Property<String> getNamespace();

    /**
     * @see CommonExtension#getCompileSdk()
     */
    @Restricted
    public abstract Property<Integer> getCompileSdk();

    /**
     * @see BaseFlavor#getMinSdk()
     */
    @Restricted
    public abstract Property<Integer> getMinSdk();

    /**
     * JDK version to use for compilation.
     */
    @Restricted
    public abstract Property<Integer> getJdkVersion();

    /**
     * Common dependencies for all targets.
     */
    @Restricted
    public LibraryDependencies getDependencies() {
        return dependencies;
    }

    @Configuring
    public void dependencies(Action<? super LibraryDependencies> action) {
        action.execute(getDependencies());
    }

    /**
     * Static targets extension block.
     */
    @Restricted
    public AndroidTargets getTargets() {
        return targets;
    }

    @Configuring
    public void targets(Action<? super AndroidTargets> action) {
        action.execute(getTargets());
    }
}
