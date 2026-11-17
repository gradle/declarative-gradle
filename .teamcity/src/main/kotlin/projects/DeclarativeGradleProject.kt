package projects

import jetbrains.buildServer.configs.kotlin.Project
import projects.unified.UnifiedPrototypeProject

object DeclarativeGradleProject : Project({
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
