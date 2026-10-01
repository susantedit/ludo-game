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
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ludora"

// App entry module
include(":app")

// Core modules
include(":core:common")
include(":core:model")
include(":core:designsystem")
include(":core:database")
include(":core:datastore")
include(":core:network")

// Game Engine modules (pure Kotlin)
include(":engine:core")
include(":engine:ludo")
include(":engine:snake")
include(":engine:ai")
include(":engine:remix")

// Feature modules
include(":feature:home")
include(":feature:ludo")
include(":feature:snake")
include(":feature:remix")
include(":feature:profile")
include(":feature:rooms")
include(":feature:settings")
