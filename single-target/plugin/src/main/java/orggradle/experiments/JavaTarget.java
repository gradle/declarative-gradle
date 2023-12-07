package orggradle.experiments;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

public interface JavaTarget {
    Property<Boolean> getDebug();

    NamedDomainObjectContainer<SourceDirectorySet> getSources();

    default void sources(Action<? super NamedDomainObjectContainer<SourceDirectorySet>> action) {
        action.execute(getSources());
    }

    @Nested
    JvmLibraryDependencies getDependencies();

    default void dependencies(Action<? super JvmLibraryDependencies> action) {
        action.execute(getDependencies());
    }
}
