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
include(":core")
include(":core:common")
include(":core:navigation")
include(":core:ui")
include(":core:settings")
include(":core:settings:api")
include(":core:settings:impl")
include(":core:network")
include(":feature-schedule")
include(":feature-schedule:api")
include(":feature-schedule:impl")
include(":feature-news")
include(":feature-search")
include(":feature-curriculum")
include(":feature-tasks")