import java.util.Properties
import java.util.regex.Pattern.compile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

val secretsProperties = Properties()
val secretsFile = rootProject.file("secrets.properties")
if (secretsFile.exists()) {
    secretsFile.inputStream().use { secretsProperties.load(it) }
}

fun secret(name: String, placeholder: String): String =
    secretsProperties.getProperty(name, placeholder)

val keystoreProperties = Properties()
val keystoreFile = rootProject.file("keystore.properties")
if (keystoreFile.exists()) {
    keystoreFile.inputStream().use { keystoreProperties.load(it) }
}

android {
    packagingOptions{
        exclude("META-INF/DEPENDENCIES")
    }
    namespace = "com.example.bookandpostroom"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bookandpostroom"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "RAZORPAY_KEY_ID", "\"${secret("RAZORPAY_KEY_ID", "YOUR_RAZORPAY_KEY_ID")}\"")
        buildConfigField("String", "RAZORPAY_KEY_SECRET", "\"${secret("RAZORPAY_KEY_SECRET", "YOUR_RAZORPAY_KEY_SECRET")}\"")
        buildConfigField("String", "RAZORPAY_TRANSFER_KEY_ID", "\"${secret("RAZORPAY_TRANSFER_KEY_ID", "YOUR_RAZORPAY_TRANSFER_KEY_ID")}\"")
        buildConfigField("String", "RAZORPAY_TRANSFER_KEY_SECRET", "\"${secret("RAZORPAY_TRANSFER_KEY_SECRET", "YOUR_RAZORPAY_TRANSFER_KEY_SECRET")}\"")

        manifestPlaceholders["MAPBOX_ACCESS_TOKEN"] = secret("MAPBOX_ACCESS_TOKEN", "YOUR_MAPBOX_ACCESS_TOKEN")
    }
    signingConfigs {
        getByName("debug") {
            val storeFilePath = keystoreProperties.getProperty("storeFile")
            if (storeFilePath != null) {
                storeFile = file(storeFilePath)
                storePassword = keystoreProperties.getProperty("storePassword", "YOUR_KEYSTORE_PASSWORD")
                keyAlias = keystoreProperties.getProperty("keyAlias", "YOUR_KEY_ALIAS")
                keyPassword = keystoreProperties.getProperty("keyPassword", "YOUR_KEY_PASSWORD")
            }
        }
    }
    buildFeatures {
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}




dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation("androidx.credentials:credentials:1.2.2")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")

    // Google ID Library (NEW)
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    // Twitter SDK (community-maintained version)
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")


    implementation ("androidx.work:work-runtime:2.7.1")
    implementation ("com.google.firebase:firebase-storage:20.0.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")


    implementation ("com.mapbox.maps:android:10.14.0")
    implementation ("com.airbnb.android:lottie:6.0.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")
    implementation ("com.razorpay:checkout:1.6.37")
    implementation ("com.google.firebase:firebase-analytics:21.3.0")
    implementation ("com.facebook.shimmer:shimmer:0.5.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation ("com.google.firebase:firebase-functions:20.4.0")


    implementation ("com.google.firebase:firebase-functions-ktx:20.4.0")


}

