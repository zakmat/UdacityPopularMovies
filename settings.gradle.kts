pluginManagement {
    plugins {
        id ("com.android.application") version "7.2.0-alpha07" apply false
        id ("com.android.library") version "7.2.0-alpha07" apply false
        id ("org.jetbrains.kotlin.android") version "1.5.31" apply false
        id ("org.jetbrains.kotlin.plugin.serialization") version "1.5.31" apply false
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

include (":app")
