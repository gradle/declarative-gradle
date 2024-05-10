package projects.unified.plugin

import common.addGradleParam
import common.configuredGradle
import jetbrains.buildServer.configs.kotlin.*

class PluginProject(private val buildAndTest: BuildType) : Project({
    id("Plugin")
    name = "Plugin"


    buildType {
        id("Deploy")
        name = "Deploy"
        description = "Deploy the Declarative Gradle unified prototype plugin"
        type = BuildTypeSettings.Type.DEPLOYMENT

        vcs {
            root(DslContext.settingsRootId)
        }

        params {
            password(
                "env.GRADLE_PUBLISH_KEY",
                "%plugin.portal.publish.key%",
                description = "The Gradle publish key to publish this plugin into"
            )
            password(
                "env.GRADLE_PUBLISH_SECRET",
                "%plugin.portal.publish.secret%",
                description = "The Gradle publish secret to publish this plugin into"
            )
        }

        requirements {
            contains("teamcity.agent.jvm.os.name", "Linux")
        }

        dependencies {
            snapshot(buildAndTest) {
                onDependencyFailure = FailureAction.FAIL_TO_START
                onDependencyCancel = FailureAction.FAIL_TO_START
            }
        }

        steps {
            step(configuredGradle {
                name = "Check"
                tasks = "check"
                workingDir = "unified-prototype"
            })
            step(configuredGradle {
                name = "Publish Plugins"
                tasks = ":unified-plugin:publishAllPlugins"
                addGradleParam("-Dgradle.publish.skip.namespace.check=true")
                workingDir = "unified-prototype"
            })
        }
    }
})
