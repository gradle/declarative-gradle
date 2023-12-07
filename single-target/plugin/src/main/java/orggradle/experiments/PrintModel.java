package orggradle.experiments;

import org.gradle.api.DefaultTask;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.compile.JavaCompile;

public abstract class PrintModel extends DefaultTask {
    @TaskAction
    void doIt() {
        var java = getProject().getExtensions().getByType(JavaPluginExtension.class);
        var mainSourceSet = java.getSourceSets().getByName("main");
        System.out.println(mainSourceSet.getJava().getSrcDirs());

        getProject().getTasks().named(mainSourceSet.getCompileJavaTaskName(), JavaCompile.class, task -> {
            System.out.println(task.getOptions().isDebug());
        });
    }
}
