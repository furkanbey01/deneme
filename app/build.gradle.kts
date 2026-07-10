plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.hourlyalarm58"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.hourlyalarm58"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2026.04.01"))
    implementation("androidx.activity:activity-compose:1.12.1")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.datastore:datastore-preferences:1.2.0")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
