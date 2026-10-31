plugins {
    id("com.android.application")
}

android {
    namespace = "io.github.mauriciogamedev.cloudhub"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.mauriciogamedev.cloudhub"
        minSdk = 26
        targetSdk = 35
        versionCode = 7
        versionName = "0.4.2"

        testInstrumentationRunner = "android.test.InstrumentationTestRunner"
    }

    testOptions {
        unitTests.all {
            it.useJUnit()
        }
    }
}

dependencies {
    implementation("androidx.webkit:webkit:1.17.1")
    testImplementation("junit:junit:4.13.2")
}
