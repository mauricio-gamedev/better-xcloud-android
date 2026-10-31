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
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "android.test.InstrumentationTestRunner"
    }

    testOptions {
        unitTests.all {
            it.useJUnit()
        }
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}
