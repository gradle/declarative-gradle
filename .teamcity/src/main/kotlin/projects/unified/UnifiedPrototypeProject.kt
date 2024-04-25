package projects.unified

import common.configuredGradle
import common.publishBuildStatusToGithub
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import projects.unified.plugin.PluginProject

object UnifiedPrototypeProject : Project({
    id("UnifiedPrototype")
    name = "Unified Prototype"

    params {
        param("env.JAVA_HOME", "%linux.java17.openjdk.64bit%")
    }

    val buildAndTest = buildType {
        id("BuildAndTest")
        name = "Build and Test"
        description = "Build and test the Declarative Gradle unified prototype"

        requirements {
            contains("teamcity.agent.jvm.os.name", "Linux")
        }

        features {
            publishBuildStatusToGithub()
        }

        vcs {
            root(DslContext.settingsRootId)
        }

        triggers {
            vcs {
                // Only run if unified-prototype or .teamcity changes
                triggerRules = """
                    +:unified-prototype/**
                    +:.teamcity/**
                """.trimIndent()

                branchFilter = """
                    +:main
                    +:pull/*/head
                """.trimIndent()
            }
        }

        steps {
            step(configuredGradle {
                name = "Build and Test"
                tasks = "build"
                workingDir = "unified-prototype"
            })
        }
    }

    subProject(PluginProject(buildAndTest))
})
