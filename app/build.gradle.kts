import java.util.Properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.raj.slotify"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.raj.slotify"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["appAuthRedirectScheme"] = "com.raj.slotify"

        buildConfigField("Boolean", "DEBUG_MODE", "false")

        buildConfigField("String", "SPRING_BASE_URL", "\"http://api.127.0.0.1.nip.io/\"")
        buildConfigField("String", "SPRING_TEST_URL", "\"http://10.0.2.2:8080/\"")

        val mapsKey = localProperties.getProperty("GOOGLE_MAPS_KEY") ?: ""
        manifestPlaceholders["GOOGLE_MAPS_KEY"] = mapsKey
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
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
        // Necesario para librerías modernas y Retrofit
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Core y UI base
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(libs.material)
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // Country code picker
    implementation("com.hbb20:ccp:2.7.2")

    // Activity y Fragment (Ktx para usar 'by activityViewModels()')
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation(libs.androidx.recyclerview)
    implementation(libs.play.services.maps)

    // Ciclo de vida (ViewModel y LiveData)
    val lifecycleVersion = "2.8.7"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    // Navegación (Jetpack Navigation)
    val navVersion = "2.8.4"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // Retrofit y JSON (Versiones estables 2.x - Recomendado para estabilidad)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Otras librerías
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("net.openid:appauth:0.11.1")

    // Soporte para legado (opcional, si lo usas)
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
