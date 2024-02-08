package org.gradle.api.experimental.android;

import com.android.build.api.dsl.BaseFlavor;
import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;

import javax.inject.Inject;

@NonNullApi
public abstract class AndroidTarget implements Named {
    private final String name;

    @Inject
    public AndroidTarget(String name) {
        this.name = name;
    }

    /**
     * @see BaseFlavor#getMinSdk()
     */
    @Input
    public abstract Property<Integer> getMinSdk();

    /**
     * Dependencies for this target.
     */
    @Nested
    public abstract LibraryDependencies getDependencies();

    public void dependencies(Action<? super LibraryDependencies> action) {
        action.execute(getDependencies());
    }

    @Override
    public String getName() {
        return name;
    }
}
