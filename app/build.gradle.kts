plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.secrets.gradle)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.alilopez.kt_demohilt"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.alilopez.kt_demohilt"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir, providers)
        buildConfigField("String", "BACKEND_URL", "\"${properties.getProperty("backend_url", "http://10.0.2.2:8000/api/")}\"")
        buildConfigField("String", "API_HOST", "\"${properties.getProperty("host", "exercisedb.p.rapidapi.com")}\"")
        buildConfigField("String", "API_KEY", "\"${properties.getProperty("key", "")}\"")
        buildConfigField("String", "EXERCISE_DB_URL", "\"${properties.getProperty("url", "https://exercisedb-api.vercel.app/api/v1/")}\"")
        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"${properties.getProperty("google_web_client_id", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }

    flavorDimensions.add("environment")
    productFlavors {
        create("dev") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL_RICK", "\"https://rickandmortyapi.com/api/\"")
            buildConfigField("String", "BASE_URL_JSON", "\"https://jsonplaceholder.typicode.com/\"")
            resValue("string", "app_name", "FitnessPro (DEV)")
        }

        create("prod") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL_RICK", "\"https://rickandmortyapi.com/api/\"")
            buildConfigField("String", "BASE_URL_JSON", "\"https://jsonplaceholder.typicode.com/\"")
            resValue("string", "app_name", "FitnessPro")
        }
    }
}

secrets {
    propertiesFileName = "local.properties"
    defaultPropertiesFileName = "local.defaults.properties"
    ignoreList.add("sdk.dir")
}

ksp {
    arg("hilt.disableModulesHaveInstallInCheck", "true")
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.com.squareup.retrofit2.retrofit)
    implementation(libs.com.squareup.retrofit2.converter.json)
    implementation(libs.io.coil.kt.coil.compose)
    implementation(libs.io.coil.kt.coil.gif)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    
    // Google Login (Nombres corregidos)
    implementation(libs.google.auth.credentials)
    implementation(libs.google.auth.play.services)
    implementation(libs.google.auth.id)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Start.io Ads
    implementation(libs.startio.sdk)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
