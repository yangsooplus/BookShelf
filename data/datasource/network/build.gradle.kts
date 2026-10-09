import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.serialization)
    id("bookshelf.android.library")
    id("bookshelf.android.hilt")
}

val localProperties = Properties().apply {
    providers.fileContents(rootProject.layout.projectDirectory.file("local.properties"))
        .asText.orNull?.let { load(it.reader()) }
}
val kakaoRestApiKey = localProperties.getProperty("KAKAO_REST_API_KEY", "").trim()

android {
    namespace = "com.yangsooplus.bookshelf.data.datasource.network"
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        val escapedKey = kakaoRestApiKey.replace("\\", "\\\\").replace("\"", "\\\"")
        buildConfigField("String", "KAKAO_REST_API_KEY", "\"$escapedKey\"")
    }
}

dependencies {
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
    testImplementation(libs.okhttp.mockwebserver)
}
