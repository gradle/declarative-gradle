package org.gradle.experimental.settings;

public interface ProjectSpecification extends ProjectContainer {
    String LOGICAL_PATH_SEPARATOR = ":";

    static String logicalPathFromParent(String logicalPathRelativeToParent, ProjectContainer parent) {
        if (parent == null) {
            throw new IllegalStateException();
        } else {
            String parentLogicalPath = parent.getLogicalPath();
            if (parentLogicalPath.equals(LOGICAL_PATH_SEPARATOR)) {
                return LOGICAL_PATH_SEPARATOR + logicalPathRelativeToParent;
            } else {
                return parentLogicalPath + LOGICAL_PATH_SEPARATOR + logicalPathRelativeToParent;
            }
        }
    }
}
