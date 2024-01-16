package orggradle.experiments;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.internal.artifacts.dsl.dependencies.DefaultDependencyAdder;
import org.gradle.api.internal.artifacts.dsl.dependencies.DependencyFactoryInternal;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

import javax.inject.Inject;
import java.lang.reflect.Field;

public abstract class DependencyCollector extends DefaultDependencyAdder {

        @Inject
        public DependencyCollector(DependencyFactoryInternal dependencyFactory, ConfigurationContainer confs) {
            super(dependencyFactory, confs.detachedConfiguration());
        }

        @Inject
        public abstract ProviderFactory getProviderFactory();

        public Provider<? extends Iterable<Dependency>> getDependencies() {
            return getProviderFactory().provider(() -> getConfiguration().getDependencies());
        }

        private Configuration getConfiguration() {
            try {
                Field configuration = this.getClass().getSuperclass().getSuperclass().getDeclaredField("configuration");
                configuration.setAccessible(true);
                return (Configuration) configuration.get(this);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
    }