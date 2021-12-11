plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31"
}
android {
    compileSdk = 31
    buildToolsVersion = "30.0.3"
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.4"
    }

    defaultConfig {
        applicationId = "com.mz.popmovies"
        minSdk = 24
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        forEach {
            val MyTmdbApiToken: String by project
            it.buildConfigField("String", "TMDB_API_TOKEN", MyTmdbApiToken)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    packagingOptions {
        resources.excludes.addAll(
            listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/*.kotlin_module"
            )
        )
    }
}

dependencies {
    implementation(fileTree("lib") { include("*.jar") })
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.preference:preference-ktx:1.1.1")
    testImplementation("junit:junit:4.13.2")
    implementation("com.google.android.material:material:1.4.0")
    implementation("io.ktor:ktor-client-android:1.6.5")
    implementation("io.ktor:ktor-client-serialization:1.6.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    implementation("io.ktor:ktor-client-logging-jvm:1.6.5")
    implementation("io.coil-kt:coil-compose:1.3.1")
    implementation("com.jakewharton.timber:timber:5.0.1")


    // Integration with activities
    implementation("androidx.activity:activity-compose:1.4.0")
    // Compose Material Design
    implementation("androidx.compose.material:material:1.0.5")
    // Animations
    implementation("androidx.compose.animation:animation:1.0.5")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:1.0.5")
    // Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")
    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.5")

}


