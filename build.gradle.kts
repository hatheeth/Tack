plugins {
    alias(libs.plugins.android.application) apply false

    // 🔑 Fix: Explicitly add the version here so Gradle can find and download it
    id("com.google.gms.google-services") version "4.4.1" apply false
}