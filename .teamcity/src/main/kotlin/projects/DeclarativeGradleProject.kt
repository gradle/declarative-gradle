package projects

import jetbrains.buildServer.configs.kotlin.Project
import projects.unified.UnifiedPrototypeProject

object DeclarativeGradleProject : Project({
    params {
        param("env.GRADLE_ENTERPRISE_ACCESS_KEY", "%e.grdev.net.access.key%")
    }

    subProject(UnifiedPrototypeProject)

    cleanup {
        baseRule {
            history(days = 14)
        }
        baseRule {
            artifacts(
                days = 7,
                artifactPatterns = "+:**/*"
            )
        }
    }
})
