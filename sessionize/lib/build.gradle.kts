import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("kotlinx-serialization")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("co.touchlab.kotlinxcodesync")
}

android {
    compileSdk = BuildConfig.compileSdk
    testOptions.unitTests.isIncludeAndroidResources = true

    defaultConfig {
        minSdk = BuildConfig.minSdk
        targetSdk = BuildConfig.targetSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        //This is for MultiplatformSettings
        debug {
            // MPP libraries don't currently get this resolution automatically
            matchingFallbacks.addAll(listOf("release"))
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType(KotlinCompile::class.java).all {
        kotlinOptions {
            jvmTarget = "11"
            useIR = true
        }
    }
}

dependencies {
    coreLibraryDesugaring(Deps.Desugar.desugar_libs)

    implementation(Deps.Android.Lifecycle.extensions)
    implementation(Deps.Android.Lifecycle.viewmodel)
    implementation(Deps.Android.Lifecycle.common)

    implementation(Deps.Android.appcompat)
    implementation(Deps.Android.core)
}

kotlin {
    android()

    // TODO: Revert to `ios()` once
    //  https://github.com/cashapp/sqldelight/issues/2044#issuecomment-721319037
    //  gets resolved
    val onPhone = System.getenv("SDK_NAME")?.startsWith("iphoneos") ?: false
    if (onPhone) {
        iosArm64("ios")
    } else {
        iosX64("ios")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Deps.Stately.common)
                implementation(Deps.Stately.concurrency)
                implementation(Deps.Stately.collections)

                implementation(Deps.SqlDelight.runtime)
                implementation(Deps.multiplatformSettings)
                implementation(Deps.Serialization.commonRuntime)
                implementation(Deps.Firebase.GitLive.firestore)
                implementation(Deps.Kotlin.Coroutines.common) {
                    version {
                        strictly(Deps.Kotlin.Coroutines.version)
                    }
                }

                implementation(Deps.Ktor.commonCore)
                implementation(Deps.Ktor.commonJson)
                implementation(Deps.Ktor.serialization)
                implementation(Deps.Ktor.commonLogging)

                implementation(Deps.Koin.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(Deps.Kotlin.Test.common)
                implementation(Deps.Kotlin.Test.annotations)
                implementation(Deps.Stately.common)
                implementation(Deps.Stately.concurrency)
                implementation(Deps.Stately.collections)
                implementation(Deps.multiplatformSettings)
                implementation(Deps.testhelp)
                implementation(Deps.Kotlin.Coroutines.common)
                implementation(Deps.Koin.test)
            }
        }

        val iosMain by getting {
            dependencies {
                implementation(Deps.SqlDelight.driverIos)
                implementation(Deps.SqLiter.ios)

                implementation(Deps.Ktor.ios)
                implementation(Deps.Ktor.native_logging)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Deps.SqlDelight.driverAndroid)
                implementation(Deps.Firebase.firestoreAndroid)

                implementation(Deps.Ktor.androidCore)
                implementation(Deps.Ktor.logback)
            }
        }

        val androidTest by getting {
            dependencies {
                implementation(Deps.Kotlin.Test.jvm)
                implementation(Deps.Kotlin.Test.junit)
                implementation(Deps.SqlDelight.driverAndroid)
                implementation(Deps.Android.Test.junit)
                implementation(Deps.Android.Test.core)
                implementation(Deps.Android.Test.junit_ext)
                implementation(Deps.Android.Test.Robolectric.core)
            }
        }
    }

    version = "1.0"
    cocoapods {
        // Configure fields required by CocoaPods.
        summary = "Lots of Droidcon Stuff"
        homepage = "https://github.com/touchlab/DroidconKotlin"
    }
}

sqldelight {
    database("DroidconDb") {
        packageName = "co.touchlab.droidcon.db"
    }
}

xcode {
    projectPath = "../../iosApp/iosApp.xcodeproj"
    target = "iosApp"
}

