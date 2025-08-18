plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.mapbox.dash.example"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mapbox.dash.example"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions.add("devMenuDefaults")
    productFlavors {
        create("Default") {
            dimension = "devMenuDefaults"
            isDefault = true
        }
        create("CustomCluster") {
            dimension = "devMenuDefaults"
        }
        create("PrivateSdk") {
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    configurations {
        all {
            exclude(group = "com.google.guava", module = "listenablefuture")
        }
    }
    packagingOptions {
        resources {
            excludes += setOf(
                "dash-sdk.properties",
                "META-INF/proguard/androidx-annotations.pro",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/kotlinx_coroutines_core.version",
                "META-INF/INDEX.LIST"
            )
        }
    }
    lint {
        checkOnly += "RestrictedApi"
        error += "RestrictedApi"
    }
}

dependencies {
    val uxfVersion = "1.2.1"
    implementation("com.mapbox.navigationux:android:$uxfVersion")
    implementation("com.mapbox.navigationux:cluster:$uxfVersion")
    implementation("com.mapbox.navigationux:weather-api:$uxfVersion")
    implementation("com.mapbox.navigationux:data-inputs:$uxfVersion")
    implementation("com.mapbox.navigationux:ev-rangemap:$uxfVersion")
    implementation("com.mapbox.navigationux:map-gpt:${uxfVersion}")
    implementation("com.mapbox.navigationux:navigation-coordination:${uxfVersion}")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.material:material")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
}
