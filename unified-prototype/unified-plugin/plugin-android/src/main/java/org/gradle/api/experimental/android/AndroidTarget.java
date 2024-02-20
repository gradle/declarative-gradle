package org.gradle.api.experimental.android;

import com.android.build.api.dsl.BaseFlavor;
import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

import javax.inject.Inject;

@Restricted
@NonNullApi
public abstract class AndroidTarget implements Named {
    private final String name;
    private final LibraryDependencies dependencies;

    @Inject
    public AndroidTarget(String name, ObjectFactory objectFactory) {
        this.name = name;
        this.dependencies = objectFactory.newInstance(LibraryDependencies.class);
    }

    /**
     * @see BaseFlavor#getMinSdk()
     */
    @Restricted
    public abstract Property<Integer> getMinSdk();

    /**
     * Dependencies for this target.
     */
    @Restricted
    public LibraryDependencies getDependencies() {
        return dependencies;
    }

    @Configuring
    public void dependencies(Action<? super LibraryDependencies> action) {
        action.execute(getDependencies());
    }

    @Override
    public String getName() {
        return name;
    }
}
