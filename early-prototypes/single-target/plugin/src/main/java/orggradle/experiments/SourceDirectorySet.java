package orggradle.experiments;

import org.gradle.api.Named;
import org.gradle.api.file.ConfigurableFileCollection;

public interface SourceDirectorySet extends Named {
        default void srcDir(String srcDir) {
            getSrcDirs().from(srcDir);
        }
        ConfigurableFileCollection getSrcDirs();
    }