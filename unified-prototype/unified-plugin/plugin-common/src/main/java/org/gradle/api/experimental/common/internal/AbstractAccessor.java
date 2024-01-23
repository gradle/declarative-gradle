package org.gradle.api.experimental.common.internal;

import groovy.lang.Closure;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.util.Configurable;
import org.gradle.util.internal.ConfigureUtil;

/**
 * Lazily applies a plugin and then configures a particular project extension presumably
 * applied by that plugin.
 *
 * Should be registered as an extension on the project. Implements the required interface
 * to allow this to be called dynamically from Groovy.
 *
 * Abstract only since we need unique types to register on the project's extension container.
 *
 * TODO: We probably don't even need to apply a plugin. `pluginClass` could just be some
 * code we run to get an instance of the `extensionClass`.
 */
public abstract class AbstractAccessor<P extends Plugin<?>, E> implements Configurable<Void> {

    private final Project project;
    private final Class<P> pluginClass;
    private final Class<E> extensionClass;

    public AbstractAccessor(Project project, Class<P> pluginClass, Class<E> extensionClass) {
        this.project = project;
        this.pluginClass = pluginClass;
        this.extensionClass = extensionClass;
    }

    // Allows Groovy to call this method with a closure
    @Override
    public Void configure(Closure cl) {
        ConfigureUtil.configureUsing(cl).execute(access());
        return null;
    }

    /**
     * Apply the plugin and return the declarative DSL model.
     */
    public E access() {
        project.getPlugins().apply(pluginClass);
        return project.getExtensions().getByType(extensionClass);
    }
}
