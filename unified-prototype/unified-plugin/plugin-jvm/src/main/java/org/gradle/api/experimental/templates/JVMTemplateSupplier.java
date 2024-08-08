package org.gradle.api.experimental.templates;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.gradle.api.file.Directory;
import org.gradle.buildinit.templates.InitProjectConfig;
import org.gradle.buildinit.templates.InitProjectGenerator;
import org.gradle.buildinit.templates.InitProjectParameter;
import org.gradle.buildinit.templates.InitProjectSpec;
import org.gradle.buildinit.templates.InitProjectSupplier;

@SuppressWarnings("UnstableApiUsage")
public class JVMTemplateSupplier implements InitProjectSupplier {
    @Override
    public List<InitProjectSpec> getProjectDefinitions() {
        return Collections.singletonList(new InitProjectSpec() {
            @Override
            public String getDisplayName() {
                return "Java Project Type";
            }

            @Override
            public List<InitProjectParameter<?>> getParameters() {
                return Collections.emptyList();
            }
        });
    }

    @Override
    public InitProjectGenerator getProjectGenerator() {
        return new InitProjectGenerator() {
            @Override
            public void generate(InitProjectConfig config, Directory location) {
                try {
                    File projectOutput = location.file("project.output").getAsFile();
                    FileUtils.writeStringToFile(projectOutput, "Hello, World!", Charset.defaultCharset());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
