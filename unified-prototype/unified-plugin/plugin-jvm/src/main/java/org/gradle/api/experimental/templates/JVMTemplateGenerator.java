package org.gradle.api.experimental.templates;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.gradle.api.file.Directory;
import org.gradle.buildinit.templates.InitProjectConfig;
import org.gradle.buildinit.templates.InitProjectGenerator;

public class JVMTemplateGenerator implements InitProjectGenerator {
    @Override
    public void generate(InitProjectConfig config, Directory location) {
        try {
            File output = location.file("project.output").getAsFile();
            output.createNewFile();
            FileWriter writer = new FileWriter(output);
            writer.write("MyGenerator created this project.");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
