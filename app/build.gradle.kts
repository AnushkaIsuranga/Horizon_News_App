plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.kahdse.horizonnewsapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kahdse.horizonnewsapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    packagingOptions {
        exclude("META-INF/INDEX.LIST")
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/io.netty.versions.properties") // Optional if using Netty
    }
}

dependencies {
    // Default Dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.appdistribution.gradle)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // API Calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

    // Material Components for user interface
    implementation("com.google.android.material:material:1.11.0")
    implementation ("com.google.android.material:material:1.9.0")

    // Navigation Components
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.5")

    // Hilt for dependency injection
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    //RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    //Scslsble Size Unit (Support for different screen sizes)
    implementation ("com.intuit.sdp:sdp-android:1.1.1")
    implementation ("com.intuit.ssp:ssp-android:1.1.1")

    //Material Design
    implementation ("com.google.android.material:material:1.9.0")

    //Rounded ImageView
    implementation ("com.makeramen:roundedimageview:2.3.0")

    //Jetpack Compose
    implementation ("androidx.compose.material:material")
    implementation ("androidx.compose.foundation:foundation")
    implementation ("androidx.compose.ui:ui")

    // Android Studio Preview support
    implementation ("androidx.compose.ui:ui-tooling-preview")
    debugImplementation ("androidx.compose.ui:ui-tooling")

    // UI Tests
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4")
    debugImplementation ("androidx.compose.ui:ui-test-manifest")

    // Optional - Integration with activities
    implementation ("androidx.activity:activity-compose:1.10.0")
    // Optional - Integration with ViewModels
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    // Optional - Integration with LiveData
    implementation ("androidx.compose.runtime:runtime-livedata")
    // Optional - Integration with RxJava
    implementation ("androidx.compose.runtime:runtime-rxjava2")
}
