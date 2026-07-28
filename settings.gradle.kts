pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

    //includeBuild("build-logic")
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "scheduleRutMiit"
include(":app")
include(":core-network")
include(":core-database")
include(":core-common")
include(":core-ui")
include(":feature-news")
include(":feature-schedule")
include(":feature-search")
include(":feature-curriculum")
include(":feature-latest-release")
include(":feature-export")
include(":feature-tasks")
