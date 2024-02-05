package org.gradle.api.experimental.android;

import com.android.build.api.dsl.BaseFlavor;
import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;

public interface AndroidTarget extends Named {

    /**
     * @see BaseFlavor#getMinSdk()
     */
    @Input
    Property<Integer> getMinSdk();

    /**
     * Dependencies for this target.
     */
    @Nested
    LibraryDependencies getDependencies();

    default void dependencies(Action<? super LibraryDependencies> action) {
        action.execute(getDependencies());
    }

}
