import java.util.Properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")// Habilitar KSP
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

        manifestPlaceholders += mapOf(
            "appAuthRedirectScheme" to "com.raj.slotify"
        )

        buildConfigField("Boolean", "DEBUG_MODE", "false")

        buildConfigField("String", "SPRING_BASE_URL", "\"http://api.10.0.2.2.nip.io/\"")
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
    // --- LIBRERÍAS DE INTERFAZ Y CORE ---
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(libs.material) // Mantén la referencia a tu catálogo si existe
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.cardview:cardview:1.0.0")

    // --- ACTIVIDAD Y FRAGMENT (FUNDAMENTAL PARA 'by activityViewModels()') ---
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.fragment:fragment-ktx:1.8.5")

    // --- CICLO DE VIDA (VIEWMODEL Y LIVEDATA) ---
    val lifecycleVersion = "2.8.7"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    // --- NAVEGACIÓN ---
    val navVersion = "2.8.4"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // --- RED: RETROFIT Y GSON ---
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // --- BASE DE DATOS: ROOM (Versión estable 2.6.1) ---
    val roomVersion = "2.8.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // --- SEGURIDAD Y AUTH (APPAUTH) ---
    // Eliminamos la duplicidad, dejamos solo una
    implementation("net.openid:appauth:0.11.1")

    // --- OTROS SERVICIOS ---
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.hbb20:ccp:2.7.2") // Country Code Picker
    implementation("androidx.preference:preference-ktx:1.2.1")

    // --- TESTING ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
