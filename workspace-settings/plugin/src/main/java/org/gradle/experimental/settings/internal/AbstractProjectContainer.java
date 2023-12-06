package org.gradle.experimental.settings.internal;

import org.gradle.api.Action;
import org.gradle.api.initialization.Settings;
import org.gradle.api.model.ObjectFactory;
import org.gradle.experimental.settings.ProjectContainer;
import org.gradle.experimental.settings.ProjectSpecification;
import org.gradle.internal.Actions;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;

abstract public class AbstractProjectContainer implements ProjectContainer {
    public static final String PROJECT_MARKER_FILE = "build.gradle.kts";
    protected static final String LOGICAL_PATH_SEPARATOR = ":";

    protected final Settings settings;
    protected final File dir;

    protected final String pathName;

    protected final ProjectContainer parent;

    public AbstractProjectContainer(Settings settings, File dir, String pathName, @Nullable ProjectContainer parent) {
        this.settings = settings;
        this.dir = dir;
        this.pathName = pathName;
        this.parent = parent;
        getAutodetect().convention(false);
    }

    public AbstractProjectContainer(Settings settings, File dir, ProjectContainer parent) {
        this(settings, dir, dir.getName(), parent);
    }

    @Override
    public File getDir() {
        return dir;
    }

    @Override
    public ProjectSpecification subproject(String relativePath) {
        return subproject(relativePath, Actions.doNothing());
    }

    @Override
    public ProjectSpecification subproject(String relativePath, Action<? super ProjectSpecification> action) {
        ProjectSpecification spec = getObjectFactory().newInstance(DefaultProjectSpecification.class, settings, relativePath, this);
        settings.include(spec.getLogicalPath());
        settings.project(spec.getLogicalPath()).setProjectDir(spec.getDir());
        action.execute(spec);
        autoDetectIfConfigured(spec);
        return spec;
    }

    @Override
    public ProjectContainer directory(String relativePath, Action<? super ProjectContainer> action) {
        DefaultDirectorySpecification spec = getObjectFactory().newInstance(DefaultDirectorySpecification.class, settings, relativePath, this);
        action.execute(spec);
        autoDetectIfConfigured(spec);
        return spec;
    }

    private void autoDetectIfConfigured(ProjectContainer container) {
        if (container.getAutodetect().get()) {
            Arrays.stream(container.getDir().listFiles())
                    .filter(file -> file.isDirectory() && new File(file, PROJECT_MARKER_FILE).exists())
                    .forEach(dir -> container.subproject(dir.getName()));
        }
    }

    @Inject
    abstract protected ObjectFactory getObjectFactory();
}
