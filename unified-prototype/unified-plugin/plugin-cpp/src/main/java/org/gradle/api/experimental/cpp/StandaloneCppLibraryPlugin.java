package org.gradle.api.experimental.cpp;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.plugins.software.SoftwareType;
import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.plugins.CppLibraryPlugin;

public abstract class StandaloneCppLibraryPlugin implements Plugin<Project> {
    @SoftwareType(name = "cppLibrary", modelPublicType = CppLibrary.class)
    abstract public CppLibrary getLibrary();

    @Override
    public void apply(Project target) {
        CppLibrary library = getLibrary();

        target.getPlugins().apply(CppLibraryPlugin.class);

        linkDslModelToPlugin(target, library);
    }

    private void linkDslModelToPlugin(Project project, CppLibrary library) {
        org.gradle.language.cpp.CppLibrary model = project.getExtensions().getByType(org.gradle.language.cpp.CppLibrary.class);

        model.getImplementationDependencies().getDependencies().addAllLater(library.getDependencies().getImplementation().getDependencies());
        model.getApiDependencies().getDependencies().addAllLater(library.getDependencies().getApi().getDependencies());

        project.afterEvaluate(p -> {
            for (CppBinary binary : model.getBinaries().get()) {
                binary.getCompileTask().get().getCompilerArgs().add(library.getCppVersion().map(v -> "--std=" + v));
            }
        });
    }
}
