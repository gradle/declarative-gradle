/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.experimental.android;

import com.android.build.api.attributes.ProductFlavorAttr;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.provider.Property;

public final class AndroidDSLSupport {
    private AndroidDSLSupport() { /* not instantiable */ }

    public static <T> void ifPresent(Property<T> property, Action<T> action) {
        if (property.isPresent()) {
            action.execute(property.get());
        }
    }

    public static void setContentTypeAttributes(Project project) {
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
