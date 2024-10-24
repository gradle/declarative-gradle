package org.gradle.experimental.settings.internal;

import org.gradle.api.Action;
import org.gradle.api.initialization.Settings;
import org.gradle.api.provider.Property;
import org.gradle.experimental.settings.ProjectContainer;
import org.gradle.experimental.settings.ProjectSpecification;
import org.gradle.internal.Actions;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.gradle.experimental.settings.ProjectSpecification.LOGICAL_PATH_SEPARATOR;
import static org.gradle.experimental.settings.ProjectSpecification.logicalPathFromParent;

public abstract class AbstractProjectContainer implements ProjectContainer {
    public static final String PROJECT_MARKER_FILE = "build.gradle.kts";

    protected final Settings settings;
    protected final File dir;

    protected final Set<File> autoDetectDirs = new HashSet<>();

    protected final String logicalPathRelativeToParent;

    protected final ProjectContainer parent;
    private final ProjectSpecificationFactory projectSpecificationFactory;

    public AbstractProjectContainer(Settings settings, File dir, String logicalPathRelativeToParent, @Nullable ProjectContainer parent, ProjectSpecificationFactory projectSpecificationFactory) {
        this.settings = settings;
        this.dir = dir;
        this.logicalPathRelativeToParent = logicalPathRelativeToParent;
        this.parent = parent;
        this.projectSpecificationFactory = projectSpecificationFactory;
        this.autoDetectDirs.add(dir);
    }

    @Override
    public File getDir() {
        return dir;
    }

    @Override
    public ProjectSpecification subproject(String logicalPath) {
        return subproject(logicalPath, Actions.doNothing());
    }

    @Override
    public ProjectSpecification subproject(String logicalPath, String relativeDirPath) {
        return subproject(logicalPath, relativeDirPath, Actions.doNothing());
    }

    @Override
    public ProjectSpecification subproject(String logicalPath, Action<? super ProjectSpecification> action) {
        return subproject(logicalPath, logicalPath, action);
    }

    @Override
    public ProjectSpecification subproject(String logicalPath, String relativeDirPath, Action<? super ProjectSpecification> action) {
        return subproject(logicalPath, new File(getDir(), relativeDirPath), action);
    }

    private ProjectSpecification subproject(File dir) {
        return subproject(dir.getName(), dir, Actions.doNothing());
    }

    public ProjectSpecification subproject(String logicalPath, File dir, Action<? super ProjectSpecification> action) {
        if (logicalPath.contains(LOGICAL_PATH_SEPARATOR) || logicalPath.contains(File.separator)) {
            throw new IllegalArgumentException("The logical path '" + logicalPath + "' should not contain separators.  To create a complex logical path, use nested calls to the 'subproject()' method.");
        }

        DefaultProjectSpecification spec = projectSpecificationFactory.create(settings, dir, logicalPath, this);
        settings.include(spec.getLogicalPath());
        settings.project(spec.getLogicalPath()).setProjectDir(spec.getDir());
        action.execute(spec);
        spec.autoDetectIfConfigured();
        return spec;
    }

    @Override
    public void from(String relativePath) {
        autoDetectDirs.add(new File(dir, relativePath));
    }

    public void autoDetectIfConfigured() {
        if (getAutodetect().get()) {
            autoDetectDirs.forEach(dir -> {
                    if (dir.exists()) {
                        Arrays.stream(dir.listFiles())
                                .filter(file -> file.isDirectory()
                                        && new File(file, PROJECT_MARKER_FILE).exists()
                                        && !projectSpecificationFactory.isLogicalPathOrDirectoryDeclared(logicalPathFromParent(file.getName(), this), file))
                                .forEach(this::subproject);
                    }
                }
            );
        }
    }

    abstract protected Property<Boolean> getAutodetect();
}
