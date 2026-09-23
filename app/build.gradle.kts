plugins {
    id("com.android.application")
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "1.9.23"

}
    android {
        namespace = "com.example.civfix" //
        compileSdk = 34

        defaultConfig {
            applicationId = "com.example.civfix"
            minSdk = 24
            targetSdk = 34
            versionCode = 1
            versionName = "1.0"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables {
                useSupportLibrary = true
            }
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
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        //  enables Jetpack Compose project
        buildFeatures {
            compose = true
        }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
    }

    dependencies {
        // Android Core & Lifecycle
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
        implementation("androidx.activity:activity-compose:1.8.2")

        // Jetpack Compose BOM
        implementation(platform("androidx.compose:compose-bom:2024.02.00"))
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.ui:ui-graphics")
        implementation("androidx.compose.ui:ui-tooling-preview")
        implementation("androidx.compose.material3:material3")
        implementation("androidx.compose.material:material-icons-extended")

        implementation("com.google.android.material:material:1.11.0")

        // Testing
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

        // Google Play Services Location for live GPS hardware tracking
        implementation("com.google.android.gms:play-services-location:21.2.0")

        // Serialization & Ktor Client (Required for Supabase)
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
        implementation("io.ktor:ktor-client-android:2.3.10")

        // SUPABASE KOTLIN SDK (Using Platform BOM Wrapper)
        implementation(platform("io.github.jan-tennert.supabase:bom:2.5.2"))
        implementation("io.github.jan-tennert.supabase:postgrest-kt")
        implementation("io.github.jan-tennert.supabase:gotrue-kt")
        implementation("io.github.jan-tennert.supabase:storage-kt")


    }
