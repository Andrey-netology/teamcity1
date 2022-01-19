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

    vcsRoot(HttpsGithubComAragastmatbExampleTeamcityGitRefsHeadsMaster)

    buildType(TestOrDeployToNexus)
    buildType(Testing)

    params {
        text("my_name", "", allowEmpty = false)
    }
}

object TestOrDeployToNexus : BuildType({
    name = "Test or Deploy to Nexus"

    artifactRules = "+:target/*.jar"

    vcs {
        root(HttpsGithubComAragastmatbExampleTeamcityGitRefsHeadsMaster)
    }

    steps {
        maven {
            name = "Deploy"

            conditions {
                contains("teamcity.build.branch", "master")
            }
            goals = "clean deploy"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "settings.xml"
        }
        maven {
            name = "Test only"

            conditions {
                doesNotContain("teamcity.build.branch", "master")
            }
            goals = "clean test"
        }
    }

    triggers {
        vcs {
        }
    }
})

object Testing : BuildType({
    name = "Testing"

    vcs {
        root(HttpsGithubComAragastmatbExampleTeamcityGitRefsHeadsMaster)
    }
})

object HttpsGithubComAragastmatbExampleTeamcityGitRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/aragastmatb/example-teamcity.git#refs/heads/master"
    url = "https://github.com/aragastmatb/example-teamcity.git"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "aragast"
        password = "credentialsJSON:0df58bae-9b65-4de0-bb4a-f24d94cb9562"
    }
})
