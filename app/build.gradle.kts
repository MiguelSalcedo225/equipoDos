plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.inventorywidget"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.inventorywidget"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    val navVersion = "2.9.0"
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    //navigation
    implementation("androidx.navigation:navigation-fragment-ktx:${navVersion}")
    implementation("androidx.navigation:navigation-ui-ktx:${navVersion}")
    implementation("androidx.navigation:navigation-common:${navVersion}")
    //cardView
    implementation("androidx.cardview:cardview:1.0.0")
    //RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    //corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    //viewmodel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("androidx.activity:activity-ktx:1.8.0")
    implementation ("androidx.fragment:fragment-ktx:1.6.2")

    // LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")

    // Room
    implementation("androidx.room:room-runtime:2.8.3")
    kapt("androidx.room:room-compiler:2.8.3")
    implementation("androidx.room:room-ktx:2.8.3")
    implementation ("com.getbase:floatingactionbutton:1.10.1")

    // SharedPreferences
    implementation("androidx.preference:preference-ktx:1.2.1")

    // Biometría
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    // Lottie (animación huella digital)
    implementation("com.airbnb.android:lottie:6.4.0")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}