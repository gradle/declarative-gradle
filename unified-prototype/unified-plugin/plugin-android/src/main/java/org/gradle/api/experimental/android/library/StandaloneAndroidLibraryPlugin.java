package org.gradle.api.experimental.android.library;

import com.android.build.api.dsl.CommonExtension;
import com.android.build.api.dsl.LibraryExtension;
import com.google.protobuf.gradle.ProtobufExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.experimental.android.AndroidBindingSupport;
import org.gradle.api.experimental.android.extensions.linting.LintSupport;
import org.gradle.api.experimental.android.library.internal.DefaultAndroidLibraryBuildModel;
import org.gradle.api.experimental.android.nia.NiaSupport;
import org.gradle.api.plugins.PluginManager;
import org.gradle.features.annotations.BindsProjectType;
import org.gradle.features.binding.ProjectTypeBinding;
import org.gradle.features.binding.ProjectTypeBindingBuilder;
import org.jetbrains.kotlin.com.google.common.base.Preconditions;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Set;

import static org.gradle.api.experimental.android.AndroidSupport.ifPresent;

/**
 * Creates a declarative {@link AndroidLibrary} DSL model, applies the official Android plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
@BindsProjectType(StandaloneAndroidLibraryPlugin.Binding.class)
public abstract class StandaloneAndroidLibraryPlugin implements Plugin<Project> {

    public static final String ANDROID_LIBRARY = "androidLibrary";

    static class Binding implements ProjectTypeBinding {
        @Override
        public void bind(ProjectTypeBindingBuilder builder) {
            builder.bindProjectType(ANDROID_LIBRARY, AndroidLibrary.class, (context, definition, buildModel) -> {
                Services services = context.getObjectFactory().newInstance(Services.class);

                // Apply the official Android plugin and support for Kotlin
                services.getPluginManager().apply("com.android.library");

                // Add the legacy extension to the build model
                ((DefaultAndroidLibraryBuildModel) buildModel).setLibraryExtension(
                        services.getProject().getExtensions().getByType(LibraryExtension.class)
                );

                // Configure android extension from the definition
                linkDefinitionToPlugin(services.getProject(), definition, buildModel, services.getProject().getConfigurations());
            })
            .withUnsafeDefinition()
            .withUnsafeApplyAction()
            .withBuildModelImplementationType(DefaultAndroidLibraryBuildModel.class);
        }

        /**
         * Performs linking actions that must occur within an afterEvaluate block.
         */
        private void linkDefinitionToPlugin(Project project, AndroidLibrary dslModel, AndroidLibraryBuildModel buildModel, ConfigurationContainer configurations) {
            LibraryExtension android = buildModel.getLibraryExtension();
            linkCommonDependencies(dslModel.getDependencies(), configurations);
            AndroidBindingSupport.linkDefinitionToPlugin(project, dslModel, android);

            configureProtobuf(project, dslModel, android);

            // TODO:DG All this configuration should be moved to the NiA project
            if (NiaSupport.isNiaProject(project)) {
                NiaSupport.configureNiaLibrary(project, dslModel);
            }
            LintSupport.configureLint(project, dslModel);
            ifPresent(dslModel.getConsumerProguardFiles(), android.getDefaultConfig()::consumerProguardFile);
            ifPresent(dslModel.getBuildConfig(), android.getBuildFeatures()::setBuildConfig);
        }

        protected void configureProtobuf(Project project, AndroidLibrary dslModel, CommonExtension android) {
            if (dslModel.getProtobuf().getEnabled().get()) {
                project.getPlugins().apply("com.google.protobuf");

                String option = dslModel.getProtobuf().getOption().getOrNull();
                project.getLogger().info("Protobuf is enabled using option=" + option + " in: " + project.getPath());
                if (Objects.equals(option, "lite")) {
                    dslModel.getDependencies().getApi().add("com.google.protobuf:protobuf-kotlin-lite:" + dslModel.getProtobuf().getVersion().get());

                    ProtobufExtension protobuf = project.getExtensions().getByType(ProtobufExtension.class);
                    String protocGAV = getProtocDepGAV(dslModel);
                    protobuf.protoc(protoc -> protoc.setArtifact(protocGAV));

                    protobuf.generateProtoTasks(generator -> {
                        generator.all().forEach(task -> {
                            task.getBuiltins().create("java", builtin -> builtin.getOptions().add("lite"));
                            task.getBuiltins().create("kotlin", builtin -> builtin.getOptions().add("lite"));
                        });
                    });
                } else {
                    throw new IllegalStateException("We only support lite option currently, not: " + option);
                }
            }
        }

        private static String getProtocDepGAV(AndroidLibrary dslModel) {
            Set<Dependency> protocDeps = dslModel.getProtobuf().getDependencies().getProtoc().getDependencies().get();
            Preconditions.checkState(protocDeps.size() == 1, "Should have a single dependency, but had: " + protocDeps.size());
            Dependency protocDep = protocDeps.iterator().next();
            return protocDep.getGroup() + ":" + protocDep.getName() + ":" + protocDep.getVersion();
        }

        @SuppressWarnings("UnstableApiUsage")
        private void linkCommonDependencies(AndroidLibraryDependencies dependencies, ConfigurationContainer configurations) {
            AndroidBindingSupport.linkCommonDependencies(dependencies, configurations);
            configurations.getByName("api").fromDependencyCollector(dependencies.getApi()); // API deps added for libraries
        }

        interface Services {
            @Inject
            PluginManager getPluginManager();

            @Inject
            Project getProject();
        }
    }

    @Override
    public void apply(Project project) {
    }
}
