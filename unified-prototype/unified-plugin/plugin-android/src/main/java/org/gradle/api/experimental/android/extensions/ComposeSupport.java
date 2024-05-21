package org.gradle.api.experimental.android.extensions;

import com.android.build.api.dsl.CommonExtension;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.experimental.android.AndroidSoftware;
import org.gradle.api.provider.Provider;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public final class ComposeSupport {
    private ComposeSupport() { /* Not instantiable */ }

    @SuppressWarnings("UnstableApiUsage")
    public static void configureCompose(Project project, AndroidSoftware dslModel, CommonExtension<?, ?, ?, ?, ?, ?> androidLib) {
        if (dslModel.getCompose().getEnabled().get()) {
            androidLib.getBuildFeatures().setCompose(true);

            androidLib.getComposeOptions().setKotlinCompilerExtensionVersion("1.5.12");

            DependencyHandler dependencies = project.getDependencies();
            dependencies.add("implementation", dependencies.platform("androidx.compose:compose-bom:2024.02.02"));
            dependencies.add("androidTestImplementation", dependencies.platform("androidx.compose:compose-bom:2024.02.02"));
            dependencies.add("implementation", "androidx.compose.ui:ui-tooling-preview");

            dslModel.getBuildTypes().getDebug().getDependencies().getImplementation().add("androidx.compose.ui:ui-tooling");

            androidLib.getTestOptions().getUnitTests().setIncludeAndroidResources(true); // For Robolectric

            project.getTasks().withType(KotlinCompile.class).configureEach(task -> {
                KotlinJvmOptions kotlinOptions = task.getKotlinOptions();
                List<String> freeCompilerArgs = new ArrayList<>();
                freeCompilerArgs.addAll(buildComposeMetricsParameters(project));
                freeCompilerArgs.addAll(stabilityConfiguration(project, dslModel));
                freeCompilerArgs.addAll(strongSkippingConfiguration(dslModel));
                kotlinOptions.setFreeCompilerArgs(freeCompilerArgs);
            });
        }
    }

    private static List<String> buildComposeMetricsParameters(Project project) {
        List<String> metricParameters = new ArrayList<>();
        Path relativePath = project.getProjectDir().toPath().relativize(project.getRootDir().toPath());
        File buildDir = project.getLayout().getBuildDirectory().get().getAsFile();

        Provider<String> enableMetricsProvider = project.getProviders().gradleProperty("enableComposeCompilerMetrics");
        boolean enableMetrics = Objects.equals(enableMetricsProvider.getOrNull(), "true");
        if (enableMetrics) {
            Path metricsFolder = buildDir.toPath().resolve("compose-metrics").resolve(relativePath);
            metricParameters.add("-P");
            metricParameters.add("plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" + metricsFolder.toAbsolutePath());
        }

        Provider<String> enableReportsProvider = project.getProviders().gradleProperty("enableComposeCompilerReports");
        boolean enableReports = Objects.equals(enableReportsProvider.getOrNull(), "true");
        if (enableReports) {
            Path reportsFolder = buildDir.toPath().resolve("compose-reports").resolve(relativePath);
            metricParameters.add("-P");
            metricParameters.add("plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" + reportsFolder.toAbsolutePath());
        }

        return metricParameters;
    }

    private static List<String> stabilityConfiguration(Project project, AndroidSoftware dslModel) {
        if (dslModel.getCompose().getStabilityConfigurationFilePath().isPresent()) {
            return Arrays.asList(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=" + project.getRootDir().getAbsolutePath() + dslModel.getCompose().getStabilityConfigurationFilePath().get()
            );
        } else {
            return Collections.emptyList();
        }
    }

    private static List<String> strongSkippingConfiguration(AndroidSoftware dslModel) {
        if (dslModel.getCompose().getExperimentalStrongSkipping().isPresent()) {
            return Arrays.asList(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:experimentalStrongSkipping=" + dslModel.getCompose().getExperimentalStrongSkipping().get()
            );
        } else {
            return Collections.emptyList();
        }
    }
}
