plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
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
        buildConfigField("Boolean", "HEADLESS_MODE_ENABLED", "false")
        buildConfigField("Boolean", "MAP_GPT_ENABLED", "true")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions.add("devMenuDefaults")
    productFlavors {
        create("Default") {
            dimension = "devMenuDefaults"
            isDefault = true
        }
        create("Headless") {
            dimension = "devMenuDefaults"
            buildConfigField("Boolean", "HEADLESS_MODE_ENABLED", "true")
            buildConfigField("Boolean", "MAP_GPT_ENABLED", "true")
        }

        create("WithoutMapGpt") {
            dimension = "devMenuDefaults"
            buildConfigField("Boolean", "HEADLESS_MODE_ENABLED", "false")
            buildConfigField("Boolean", "MAP_GPT_ENABLED", "false")
        }

        create("CustomCluster") {
            dimension = "devMenuDefaults"
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE-notice.md")
            excludes.add("META-INF/INDEX.LIST")
        }
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    lint {
        checkOnly += "RestrictedApi"
        error += "RestrictedApi"
    }
}

dependencies {
    val uxfVersion = "1.25.2"
    implementation("com.mapbox.navigationux:android:$uxfVersion")
    implementation("com.mapbox.navigationux:cluster:$uxfVersion")
    implementation("com.mapbox.navigationux:data-inputs:$uxfVersion")
    implementation("com.mapbox.navigationux:ev-rangemap:$uxfVersion")
    implementation("com.mapbox.navigationux:map-gpt:${uxfVersion}")
    implementation("com.mapbox.navigationux:ev-driver-notification:$uxfVersion")
    implementation("com.mapbox.navigationux:voice-feedback:$uxfVersion")

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation(platform("androidx.compose:compose-bom:2025.07.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.fragment:fragment-compose:1.8.9")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
}
