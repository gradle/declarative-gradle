plugins {
    id("base")
}

tasks.named("check") {
    dependsOn(gradle.includedBuild("unified-plugin").task(":check"))
}
