package org.gradle.api.experimental.android;

import com.android.build.api.dsl.BaseFlavor;
import com.h0tk3y.kotlin.staticObjectNotation.Configuring;
import com.h0tk3y.kotlin.staticObjectNotation.Restricted;
import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.experimental.common.RestrictedLibraryDependencies;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

@Restricted
@NonNullApi
public abstract class AndroidTarget implements Named {
    private final String name;
    private final RestrictedLibraryDependencies dependencies;

    @Inject
    public AndroidTarget(String name, ObjectFactory objectFactory) {
        this.name = name;
        this.dependencies = objectFactory.newInstance(RestrictedLibraryDependencies.class);
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
    public RestrictedLibraryDependencies getDependencies() {
        return dependencies;
    }

    @Configuring
    public void dependencies(Action<? super RestrictedLibraryDependencies> action) {
        action.execute(getDependencies());
    }

    @Override
    public String getName() {
        return name;
    }
}
