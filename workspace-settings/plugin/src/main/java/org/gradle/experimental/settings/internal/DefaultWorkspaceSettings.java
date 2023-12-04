package org.gradle.experimental.settings.internal;

import org.gradle.api.Action;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.file.FileCollectionFactory;
import org.gradle.api.model.ObjectFactory;
import org.gradle.experimental.settings.AutoDetectSettings;
import org.gradle.experimental.settings.ProjectContainer;
import org.gradle.experimental.settings.RootBuildSpecification;
import org.gradle.experimental.settings.WorkspaceSettings;
import org.gradle.internal.Actions;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

abstract public class DefaultWorkspaceSettings implements WorkspaceSettings {
    private static final Set<String> IGNORED_DIRECTORIES = Set.of("build", ".gradle", ".idea", ".git", ".svn", ".hg", "CVS");
    public static final String PROJECT_MARKER_FILE = "build.gradle.kts";
    private final Settings settings;
    private final DefaultAutoDetectSettings autoDetectSettings;
    private boolean buildConfigured;

    @Inject
    public DefaultWorkspaceSettings(Settings settings) {
        this.settings = settings;
        this.autoDetectSettings = getObjectFactory().newInstance(DefaultAutoDetectSettings.class);
    }

    @Override
    public RootBuildSpecification build(Action<? super RootBuildSpecification> action) {
        if (buildConfigured) {
            throw new UnsupportedOperationException("The build can only be configured once");
        }
        buildConfigured = true;

        RootBuildSpecification spec = getObjectFactory().newInstance(DefaultRootBuildSpecification.class, settings);
        action.execute(spec);
        settings.getRootProject().setName(spec.getName().get());
        return spec;
    }

    /**
     * If we find foo/bar/baz/build.gradle.kts, should we configure this as :foo:bar:baz?
     */
    //@Override
//    public void autoDetectIfNotConfigured() {
//        if (!buildConfigured) {
//            build(settings.getRootProject().getName(), rootBuildSpecification -> {
//                ConfigurableFileTree fileTree = getFileCollectionFactory().fileTree();
//                fileTree.setDir(settings.getRootDir());
//                if (autoDetectSettings.getIncludes().isEmpty()) {
//                    fileTree.include("*/**/" + PROJECT_MARKER_FILE);
//                } else {
//                    Set<String> includes = autoDetectSettings.getIncludes().stream()
//                            .map(pattern -> pattern + "/" + PROJECT_MARKER_FILE)
//                            .collect(Collectors.toSet());
//                    fileTree.include(includes);
//                }
//                IGNORED_DIRECTORIES.forEach(fileTree::exclude);
//                if (!autoDetectSettings.getExcludes().isEmpty()) {
//                    fileTree.exclude(autoDetectSettings.getExcludes());
//                }
//
//                Map<Path, ProjectContainer> containers = new HashMap<>();
//                fileTree.getFiles().forEach(file -> {
//                    Path segments = settings.getRootDir().toPath().relativize(file.getParentFile().toPath());
//                    Path parentPath = null;
//                    for (Path segment : segments) {
//                        ProjectContainer parent;
//                        Path currentPath;
//                        if (parentPath == null) {
//                            parent = rootBuildSpecification;
//                            currentPath = segment;
//                        } else {
//                            parent = containers.get(parentPath);
//                            currentPath = parentPath.resolve(segment);
//                        }
//                        containers.computeIfAbsent(currentPath, path -> parent.subproject(segment.getFileName().toString()));
//                        parentPath = currentPath;
//                    }
//                });
//            });
//        }
//    }

    @Inject
    abstract protected FileCollectionFactory getFileCollectionFactory();

    @Inject
    abstract protected ObjectFactory getObjectFactory();

    static class DefaultAutoDetectSettings implements AutoDetectSettings {
        private final Set<String> includes = new HashSet<>();
        private final Set<String> excludes = new HashSet<>();

        @Inject
        public DefaultAutoDetectSettings() {
        }

        @Override
        public void include(String directoryPattern) {
            includes.add(directoryPattern);
        }

        @Override
        public void exclude(String directoryPattern) {
            excludes.add(directoryPattern);
        }

        public Set<String> getIncludes() {
            return includes;
        }

        public Set<String> getExcludes() {
            return excludes;
        }
    }
}
