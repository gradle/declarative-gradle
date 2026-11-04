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
        param("env.ANDROID_HOME", "/opt/android/sdk")
        param("env.ANDROID_SDK_ROOT", "/opt/android/sdk")
        // Add Swift to the path
        param("env.PATH", "/opt/swift/latest/usr/bin:%env.PATH%")
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
            // Pull in all PRs; the branch filter decides which ones auto-trigger
            pullRequests {
                vcsRootExtId = DslContext.settingsRootId.toString()
                provider = github {
                    authType = token {
                        token = "%github.bot-teamcity.token%"
                    }
                    filterAuthorRole = PullRequests.GitHubRoleFilter.EVERYBODY
                    ignoreDrafts = false
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

                // Fork PRs are not auto-triggered: they would run build steps as
                // defined in the PR branch. Members' PRs are, forks stay manual.
                branchFilter = """
                    +:<default>
                    +:devprod/*
                    -:dependabot/*
                    +pr: github_role=MEMBER
                    -pr: source=dependabot/*
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
