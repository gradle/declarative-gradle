package org.gradle.api.experimental.common

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import java.io.File
import java.nio.charset.Charset

abstract class SourceFileCollection {
    @get:InputFiles
    abstract val files: ConfigurableFileCollection

    @get:Input
    abstract val encoding: Property<Charset>
}

operator fun SourceFileCollection.plusAssign(dir: File) {
    this.files.from(dir)
}

operator fun SourceFileCollection.plusAssign(dirs: FileCollection) {
    this.files.from(dirs)
}

operator fun SourceFileCollection.plusAssign(dir: Provider<File>) {
    this.files.from(dir)
}

operator fun SourceFileCollection.plusAssign(dir: SourceFileCollection) {
    this.files.from(dir.files)
}