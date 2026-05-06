package org.gradle.api.experimental.java.checkstyle;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.DependencyScopeConfiguration;
import org.gradle.api.artifacts.ResolvableConfiguration;
import org.gradle.api.artifacts.dsl.DependencyFactory;
import org.gradle.api.experimental.jvm.JavaBuildModel;
import org.gradle.api.plugins.quality.Checkstyle;
import org.gradle.api.plugins.quality.CheckstylePlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.features.annotations.BindsProjectFeature;
import org.gradle.features.binding.Definition;
import org.gradle.features.binding.ProjectFeatureApplicationContext;
import org.gradle.features.binding.ProjectFeatureApplyAction;
import org.gradle.features.binding.ProjectFeatureBinding;
import org.gradle.features.binding.ProjectFeatureBindingBuilder;
import org.gradle.features.file.ProjectFeatureLayout;
import org.gradle.features.registration.ConfigurationRegistrar;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

import javax.inject.Inject;

@SuppressWarnings("UnstableApiUsage")
@BindsProjectFeature(CheckstyleSoftwareFeaturePlugin.Binding.class)
public class CheckstyleSoftwareFeaturePlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {

    }

    static class Binding implements ProjectFeatureBinding {
        @Override
        public void bind(ProjectFeatureBindingBuilder builder) {
            builder.bindProjectFeature(
                "checkstyle",
                ProjectFeatureBindingBuilder.bindingToTargetBuildModel(CheckstyleDefinition.class, JavaBuildModel.class),
                ApplyAction.class
            )
            .withUnsafeApplyAction();
        }

        @SuppressWarnings("UnstableApiUsage")
        static abstract class ApplyAction implements ProjectFeatureApplyAction<CheckstyleDefinition, CheckstyleBuildModel, Definition<JavaBuildModel>> {
            @Inject
            public ApplyAction() {
            }

            @Inject
            protected abstract ConfigurationRegistrar getConfigurationRegistrar();

            @Inject
            protected abstract ProjectFeatureLayout getProjectFeatureLayout();

            @Inject
            protected abstract DependencyFactory getDependencyFactory();

            @Inject
            protected abstract Project getProject();

            @Override
            public void apply(ProjectFeatureApplicationContext context, CheckstyleDefinition definition, CheckstyleBuildModel buildModel, Definition<JavaBuildModel> parent) {
                JavaBuildModel javaBuildModel = context.getBuildModel(parent);

                buildModel.getCheckstyleVersion().set(definition.getCheckstyleVersion().orElse(CheckstylePlugin.DEFAULT_CHECKSTYLE_VERSION));
                buildModel.getConfigDirectory().set(definition.getConfigDirectory().orElse(getProjectFeatureLayout().getSettingsDirectory().dir("config")));
                buildModel.getConfigFile().set(definition.getConfigFile().orElse(buildModel.getConfigDirectory().file("checkstyle.xml")));

                linkCheckstyle(buildModel, javaBuildModel, getConfigurationRegistrar(), getProject().getTasks(), getDependencyFactory(), getProjectFeatureLayout());
            }

            private void linkCheckstyle(CheckstyleBuildModel buildModel, JavaBuildModel javaBuildModel, ConfigurationRegistrar configurations, TaskContainer tasks, DependencyFactory dependencyFactory, ProjectFeatureLayout projectLayout) {
                // Half implementation of org.gradle.checkstyle
                NamedDomainObjectProvider<DependencyScopeConfiguration> checkstyleTool = configurations.dependencyScope("checkstyleTool", conf -> {
                    conf.getDependencies().addLater(buildModel.getCheckstyleVersion().map(v -> dependencyFactory.create("com.puppycrawl.tools:checkstyle:" + v)));
                });
                NamedDomainObjectProvider<ResolvableConfiguration> checkstyleClasspath = configurations.resolvable("checkstyleClasspath", conf -> {
                    conf.extendsFrom(checkstyleTool.get());
                });

                SourceSetContainer sourceSets = javaBuildModel.getJavaPluginExtension().getSourceSets();
                buildModel.getClasspath().from(checkstyleClasspath);
                sourceSets.all(sourceSet -> createCheckstyleForSourceSet(buildModel, sourceSet, tasks, projectLayout));
            }

            private static void createCheckstyleForSourceSet(CheckstyleBuildModel buildModel, SourceSet sourceSet, TaskContainer tasks, ProjectFeatureLayout projectLayout) {
                TaskProvider<Checkstyle> checkstyleTask = tasks.register(sourceSet.getTaskName("checkstyle", null), Checkstyle.class, task -> {
                    task.setDescription(String.format("Runs Checkstyle for source set '%s'.", sourceSet.getName()));
                    task.setGroup(LifecycleBasePlugin.VERIFICATION_GROUP);

                    task.setSource(sourceSet.getAllJava());
                    task.setClasspath(sourceSet.getRuntimeClasspath());

                    task.getConfigDirectory().convention(buildModel.getConfigDirectory());
                    task.setCheckstyleClasspath(buildModel.getClasspath());
                    task.setConfigFile(buildModel.getConfigFile().getAsFile().get());

                    task.getReports().getHtml().getRequired().convention(true);
                    task.getReports().getHtml().getOutputLocation().convention(projectLayout.getContextBuildDirectory().map(layout -> layout.file("reports/checkstyle/" + sourceSet.getName() + ".html")));

                    task.getReports().getXml().getRequired().convention(task.getReports().getHtml().getRequired());
                    task.getReports().getXml().getOutputLocation().convention(projectLayout.getContextBuildDirectory().map(layout -> layout.file("reports/checkstyle/" + sourceSet.getName() + ".xml")));
                });

                tasks.named("check").configure(check -> {
                    check.dependsOn(checkstyleTask);
                });
            }
        }
    }
}
