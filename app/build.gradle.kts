import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-parcelize")
}

android {
    namespace = "com.mapbox.dash.showcase.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mapbox.dash.example"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        buildConfigField(
            "Boolean", "HEADLESS_MODE_ENABLED",
            providers.gradleProperty("APP_SHOWCASE_HEADLESS_MODE_ENABLED").getOrElse("false"),
        )
        buildConfigField(
            "Boolean", "MAP_GPT_ENABLED",
            providers.gradleProperty("APP_SHOWCASE_MAP_GPT_MODE_ENABLED").getOrElse("true"),
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions.add("sdkType")
    productFlavors {
        create("Default") {
            dimension = "sdkType"
            isDefault = true
        }
        create("PrivateSdk") {
            dimension = "sdkType"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE-notice.md")
            excludes.add("META-INF/INDEX.LIST")
        }
    }
    lint {
        checkOnly += "RestrictedApi"
        error += "RestrictedApi"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

androidComponents {
    onVariants { variant ->
        if (providers.gradleProperty("APP_SHOWCASE_CUSTOM_CLUSTER").orNull.toBoolean()) {
            variant.sources.manifests.addStaticManifestFile("src/CustomCluster/AndroidManifest.xml")
        }
    }
}

dependencies {
    val uxfVersion = "1.36.0"
    implementation("com.mapbox.navigationux:android:$uxfVersion")
    implementation("com.mapbox.navigationux:cluster:$uxfVersion")
    privateSdkImplementation("com.mapbox.navigationux:data-inputs:$uxfVersion")
    privateSdkImplementation("com.mapbox.navigationux:ev-rangemap:$uxfVersion")
    implementation("com.mapbox.navigationux:map-gpt:${uxfVersion}")
    privateSdkImplementation("com.mapbox.navigationux:ev-driver-notification:$uxfVersion")
    implementation("com.mapbox.navigationux:voice-feedback:$uxfVersion")

    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation(platform("androidx.compose:compose-bom:2026.06.01"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
}

fun DependencyHandler.privateSdkImplementation(dependencyNotation: String) {
    add("PrivateSdkImplementation", dependencyNotation)
}
