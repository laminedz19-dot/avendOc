import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy
import groovy.json.JsonSlurper

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.example.admin"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.avendoc.admin"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug { signingConfig = signingConfigs.getByName("debugConfig") }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

val expectedFirebasePackage = android.defaultConfig.applicationId
val firebaseConfigFile = file("google-services.json")
val validateGoogleServices by tasks.registering {
  group = "verification"
  description = "Validates google-services.json against the Android application configuration."
  notCompatibleWithConfigurationCache("Reads the Firebase JSON during task execution.")

  doLast {
    val configFile = firebaseConfigFile
    check(configFile.isFile) {
      "Firebase configuration missing: ${configFile.path}. Add the file for this app before building."
    }
    val root = JsonSlurper().parseText(configFile.readText()) as? Map<*, *>
      ?: error("Firebase configuration is not a JSON object: ${configFile.path}")
    val projectInfo = root["project_info"] as? Map<*, *>
      ?: error("Firebase configuration is missing project_info.")
    val projectId = projectInfo["project_id"] as? String
    check(!projectId.isNullOrBlank()) { "Firebase configuration is missing project_info.project_id." }
    val clients = root["client"] as? List<*>
      ?: error("Firebase configuration is missing client[].")
    val matchingClient = clients.asSequence()
      .mapNotNull { it as? Map<*, *> }
      .firstOrNull { client ->
        val info = client["client_info"] as? Map<*, *>
        val androidInfo = info?.get("android_client_info") as? Map<*, *>
        androidInfo?.get("package_name") == expectedFirebasePackage
      }
    check(matchingClient != null) {
      "Firebase config package mismatch. Expected: $expectedFirebasePackage. " +
        "No matching client[].client_info.android_client_info.package_name found."
    }
    val matchingClientInfo = matchingClient["client_info"] as? Map<*, *>
    val mobileSdkAppId = (matchingClient["mobilesdk_app_id"] as? String)
      ?: (matchingClientInfo?.get("mobilesdk_app_id") as? String)
    check(!mobileSdkAppId.isNullOrBlank()) {
      "Firebase configuration is missing client[].mobilesdk_app_id."
    }
    println("Firebase configuration validated for $expectedFirebasePackage (project: $projectId).")
  }
}

tasks.named("preBuild") { dependsOn(validateGoogleServices) }

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  // implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  implementation(libs.firebase.auth)
  implementation(libs.firebase.firestore)
  implementation(libs.firebase.messaging)

  // Uncomment ALL FOUR of the following dependencies together to use Google
  // Sign-In via Credential Manager:
  // implementation(libs.androidx.credentials)
  // implementation(libs.androidx.credentials.play.services)
  // implementation(libs.googleid)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}
