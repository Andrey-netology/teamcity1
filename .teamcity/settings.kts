import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2021.2"

project {

    vcsRoot(HttpsGithubComAndreyNetologyTeamcity1gitRefsHeadsMain)

    buildType(BuildDeploy)
    buildType(First)
}

object BuildDeploy : BuildType({
    name = "Build&deploy"

    vcs {
        root(HttpsGithubComAndreyNetologyTeamcity1gitRefsHeadsMain)
    }

    steps {
        maven {

            conditions {
                doesNotContain("teamcity.build.branch", "main")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "custom.xml"
        }
        maven {
            name = "Deploy to nexus"

            conditions {
                contains("teamcity.build.branch", "main")
            }
            goals = "clean deploy"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "custom.xml"
        }
    }

    triggers {
        vcs {
        }
    }
})

object First : BuildType({
    name = "first"

    vcs {
        root(DslContext.settingsRoot)
    }
})

object HttpsGithubComAndreyNetologyTeamcity1gitRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/Andrey-netology/teamcity1.git#refs/heads/main"
    url = "https://github.com/Andrey-netology/teamcity1.git"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
})
