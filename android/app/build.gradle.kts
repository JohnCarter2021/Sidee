plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.chaquo.python")
}

android {
    namespace = "io.github.johncarter2021.sidee"
    compileSdk = 34

    defaultConfig {
        // Spike application ID (fork owner). A release identifier is
        // still an open decision; see android/README.md.
        applicationId = "io.github.johncarter2021.sidee"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.4.2"
        ndk {
            // Chaquopy ABIs: arm64 for phones, x86_64 for emulators.
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

chaquopy {
    defaultConfig {
        // Chaquopy 16 supports up to 3.13; 3.11 is chosen for the
        // widest wheel availability. paho-mqtt is pure Python.
        version = "3.11"
        pip {
            install("paho-mqtt==2.1.0")
        }
        // Keep .py sources in the spike APK so stack traces are
        // readable; startup speed is not the spike's question.
        pyc {
            src = false
        }
    }
}

// Copy the shared Python core into the Chaquopy source tree at build
// time, instead of committing a second copy that would drift. The
// generated tree is gitignored; only sidee_android.py is committed
// under app/src/main/python.
val copyCore = tasks.register<Copy>("copyCore") {
    from("../../core") {
        include("*.py")
        include("dashboard.html")
        include("tv-client.bundle")
    }
    into("src/main/python/core")
    from("../../assets") {
        include("*.png")
    }
    into("src/main/python/assets")
}

tasks.named("preBuild") {
    dependsOn(copyCore)
}

dependencies {
}
