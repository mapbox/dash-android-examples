pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// This will find your MAPBOX_DOWNLOADS_TOKEN in ~/.gradle/gradle.properties
val MAPBOX_DOWNLOADS_TOKEN: String? by settings
val SDK_REGISTRY_TOKEN: String? by settings

dependencyResolutionManagement {
    val token = MAPBOX_DOWNLOADS_TOKEN
        ?: SDK_REGISTRY_TOKEN
        ?: System.getenv("MAPBOX_DOWNLOADS_TOKEN").takeIf { !it.isNullOrBlank() }
        ?: System.getenv("SDK_REGISTRY_TOKEN").takeIf { !it.isNullOrBlank() }
    checkNotNull(token) {
        "'MAPBOX_DOWNLOADS_TOKEN' or 'SDK_REGISTRY_TOKEN' should be defined in gradle properties or env variables"
    }
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                password = token
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

rootProject.name = "Dash Examples"
include(":app")
