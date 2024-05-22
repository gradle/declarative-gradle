package org.gradle.api.experimental.android.application;

import com.android.build.api.attributes.ProductFlavorAttr;
import com.android.build.api.dsl.ApplicationExtension;
import org.gradle.api.Project;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.experimental.android.AbstractAndroidSoftwarePlugin;
import org.gradle.api.experimental.android.AndroidSoftware;
import org.gradle.api.experimental.android.nia.NiaSupport;
import org.gradle.api.internal.plugins.software.SoftwareType;

import java.util.Objects;

/**
 * Creates a declarative {@link AndroidApplication} DSL model, applies the official Android plugin,
 * and links the declarative model to the official plugin.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class StandaloneAndroidApplicationPlugin extends AbstractAndroidSoftwarePlugin {
    @SoftwareType(name = "androidApplication", modelPublicType=AndroidApplication.class)
    public abstract AndroidApplication getAndroidApplication();

    @Override
    protected AndroidSoftware getAndroidSoftware() {
        return getAndroidApplication();
    }

    @Override
    public void apply(Project project) {
        super.apply(project);

        AndroidApplication dslModel = getAndroidApplication();

        // Register an afterEvaluate listener before we apply the Android plugin to ensure we can
        // run actions before Android does.
        project.afterEvaluate(p -> linkDslModelToPlugin(p, dslModel));

        // Apply the official Android plugin.
        project.getPlugins().apply("com.android.application");
        project.getPlugins().apply("org.jetbrains.kotlin.android");

        // After AGP creates configurations, link deps to the collectors
        linkCommonDependencies(dslModel.getDependencies(), project.getConfigurations());
    }

    /**
     * Performs linking actions that must occur within an afterEvaluate block.
     */
    private void linkDslModelToPlugin(Project project, AndroidApplication dslModel) {
        ApplicationExtension android = project.getExtensions().getByType(ApplicationExtension.class);
        linkDslModelToPlugin(project, dslModel, android);

        android.defaultConfig(defaultConfig -> {
            ifPresent(dslModel.getVersionCode(), defaultConfig::setVersionCode);
            ifPresent(dslModel.getVersionName(), defaultConfig::setVersionName);
            ifPresent(dslModel.getApplicationId(), defaultConfig::setApplicationId);
            return null;
        });

        // TODO: All this configuration should be moved to the NiA project
        if (Objects.equals(project.getRootProject().getName(), NiaSupport.NIA_PROJECT_NAME)) {
            // ProductFlavors are automatically added by the LIBRARY plugin via NiA support only, ATM, so we
            // need to make sure any Android APPLICATION projects have the necessary attributes for project deps to work.
            configureContentTypeAttributes(project);

            NiaSupport.configureNiaApplication(project, dslModel);
        }
    }

    private void configureContentTypeAttributes(Project project) {
        // These attributes must be set to avoid Ambiguous Variants resolution errors between the
        // demoDebugRuntimeElements and prodDebugRuntimeElements for project dependencies in NiA
        project.getConfigurations().configureEach(c -> {
            AttributeContainer attributes = c.getAttributes();
            String lowerConfName = c.getName().toLowerCase();

            Attribute<ProductFlavorAttr> contentTypeFlavorAttr = ProductFlavorAttr.of("contentType");
            if (!attributes.contains(contentTypeFlavorAttr)) {
                if (lowerConfName.contains("debug")) {
                    attributes.attribute(contentTypeFlavorAttr, project.getObjects().named(ProductFlavorAttr.class, "demo"));
                } else if (lowerConfName.contains("release")) {
                    attributes.attribute(contentTypeFlavorAttr, project.getObjects().named(ProductFlavorAttr.class, "prod"));
                }
            }

            Attribute<String> contentTypeStringAttr = Attribute.of("contentType", String.class);
            if (!attributes.contains(contentTypeStringAttr)) {
                if (lowerConfName.contains("debug")) {
                    attributes.attribute(contentTypeStringAttr, "demo");
                } else if (lowerConfName.contains("release")) {
                    attributes.attribute(contentTypeStringAttr, "prod");
                }
            }
        });
    }
}
