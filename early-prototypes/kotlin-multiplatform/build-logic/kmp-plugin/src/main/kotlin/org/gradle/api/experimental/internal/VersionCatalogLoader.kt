package org.gradle.api.experimental.internal

import org.gradle.api.Project
import org.gradle.api.internal.GradleInternal
import org.gradle.api.internal.catalog.DefaultVersionCatalog
import org.gradle.api.internal.catalog.DefaultVersionCatalogBuilder

object VersionCatalogLoader {
    @JvmStatic
    fun loadPluginVersionCatalog(project: Project): DefaultVersionCatalog {
        val pluginCatalogFile = ResourceLoader.getResourceAsTempFile("META-INF/catalogs/libs.versions.toml")

        val vcs = (project.gradle as GradleInternal).settings.dependencyResolutionManagement.versionCatalogs
        val pluginLibs = (vcs.create("pluginLibs") {
            from(project.files(pluginCatalogFile))
        } as DefaultVersionCatalogBuilder).build()
        return pluginLibs
    }
}