import java.util.Properties

plugins {
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
    api(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
    testImplementation(libs.okhttp.mockwebserver)
}
