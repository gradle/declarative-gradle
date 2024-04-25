package common

import jetbrains.buildServer.configs.kotlin.BuildFeatures
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher


fun BuildFeatures.publishBuildStatusToGithub() {
    commitStatusPublisher {
        publisher = github {
            githubUrl = "https://api.github.com"
            authType = personalToken {
                token = "%github.bot-gradle.token%"
            }
        }
    }
}
