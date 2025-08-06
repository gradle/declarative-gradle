package org.gradle.api.experimental.java.checkstyle;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.DependencyScopeConfiguration;
import org.gradle.api.artifacts.ResolvableConfiguration;
import org.gradle.api.artifacts.dsl.DependencyFactory;
import org.gradle.api.experimental.jvm.JavaBuildModel;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.internal.plugins.*;
import org.gradle.api.plugins.quality.Checkstyle;
import org.gradle.api.plugins.quality.CheckstylePlugin;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("UnstableApiUsage")
@BindsSoftwareFeature(CheckstyleSoftwareFeaturePlugin.Binding.class)
public class CheckstyleSoftwareFeaturePlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {

    }

    static class Binding implements SoftwareFeatureBindingRegistration {
        @Override
        public void register(SoftwareFeatureBindingBuilder builder) {
            builder.bindSoftwareFeature(
                    "checkstyle",
                    SoftwareFeatureBindingBuilder.bindingToTargetBuildModel(CheckstyleDefinition.class, JavaBuildModel.class),
                    (context, definition, buildModel, parent) -> {
                        Project project = context.getProject();
                        JavaBuildModel javaBuildModel = context.getOrCreateModel(parent);

                        definition.getCheckstyleVersion().convention(CheckstylePlugin.DEFAULT_CHECKSTYLE_VERSION);
                        definition.getConfigDirectory().convention(context.getProjectLayout().getSettingsDirectory().dir("config"));
                        definition.getConfigFile().convention(definition.getConfigDirectory().file("checkstyle.xml"));

                        buildModel.getConfigDirectory().convention(definition.getConfigDirectory());
                        buildModel.getConfigFile().convention(definition.getConfigFile());

                        linkCheckstyle(buildModel, definition.getCheckstyleVersion(), javaBuildModel, project.getConfigurations(), project.getTasks(), project.getDependencyFactory(), project.getLayout());
                    });
        }

        public static void linkCheckstyle(CheckstyleBuildModel buildModel, Property<String> checkStyleVersion, JavaBuildModel javaBuildModel, ConfigurationContainer configurations, TaskContainer tasks, DependencyFactory dependencyFactory, ProjectLayout projectLayout) {
            // Half implementation of org.gradle.checkstyle
            NamedDomainObjectProvider<DependencyScopeConfiguration> checkstyleTool = configurations.dependencyScope("checkstyleTool", conf -> {
                conf.getDependencies().addLater(checkStyleVersion.map(v -> dependencyFactory.create("com.puppycrawl.tools:checkstyle:" + v)));
            });
            NamedDomainObjectProvider<ResolvableConfiguration> checkstyleClasspath = configurations.resolvable("checkstyleClasspath", conf -> {
                conf.extendsFrom(checkstyleTool.get());
            });

            SourceSetContainer sourceSets = javaBuildModel.getJavaPluginExtension().getSourceSets();
            buildModel.getClasspath().from(checkstyleClasspath);
            sourceSets.all(sourceSet -> createCheckstyleForSourceSet(buildModel, sourceSet, tasks, projectLayout));
        }

        private static void createCheckstyleForSourceSet(CheckstyleBuildModel buildModel, SourceSet sourceSet, TaskContainer tasks, ProjectLayout projectLayout) {
            TaskProvider<@NonNull Checkstyle> checkstyleTask = tasks.register(sourceSet.getTaskName("checkstyle", null), Checkstyle.class, task -> {
                task.setDescription(String.format("Runs Checkstyle for source set '%s'.", sourceSet.getName()));
                task.setGroup(LifecycleBasePlugin.VERIFICATION_GROUP);

                task.setSource(sourceSet.getAllJava());
                task.setClasspath(sourceSet.getRuntimeClasspath());

                task.getConfigDirectory().convention(buildModel.getConfigDirectory());
                task.setCheckstyleClasspath(buildModel.getClasspath());
                task.setConfigFile(buildModel.getConfigFile().getAsFile().get());

                task.getReports().getHtml().getRequired().convention(true);
                task.getReports().getHtml().getOutputLocation().convention(projectLayout.getBuildDirectory().file("reports/checkstyle/" + sourceSet.getName() + ".html"));

                task.getReports().getXml().getRequired().convention(task.getReports().getHtml().getRequired());
                task.getReports().getXml().getOutputLocation().convention(projectLayout.getBuildDirectory().file("reports/checkstyle/" + sourceSet.getName() + ".xml"));
            });

            tasks.named("check").configure(check -> {
                check.dependsOn(checkstyleTask);
            });
        }
    }
}
