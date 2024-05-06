package projects.unified

import common.configuredGradle
import common.publishBuildStatusToGithub
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import projects.unified.plugin.PluginProject

object UnifiedPrototypeProject : Project({
    id("UnifiedPrototype")
    name = "Unified Prototype"

    params {
        param("env.JAVA_HOME", "%linux.java17.openjdk.64bit%")
        param("env.ANDROID_HOME", "/opt/android/sdk")
        param("env.ANDROID_SDK_ROOT", "/opt/android/sdk")
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
            // Pull in all PRs, this will not trigger on them but allows manual runs
            pullRequests {
                vcsRootExtId = DslContext.settingsRootId.toString()
                provider = github {
                    authType = token {
                        token = "%github.bot-gradle.declarative-gradle.token%"
                    }
                    filterAuthorRole = PullRequests.GitHubRoleFilter.EVERYBODY
                }
            }
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

                // Trigger on all branches, specifically not `refs/pull/*` as we only want to run automatically on
                // our own content.
                branchFilter = """
                    +:refs/heads/*
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
