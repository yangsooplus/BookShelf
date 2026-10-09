plugins {
    id("bookshelf.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.yangsooplus.bookshelf.feature.book"
}

dependencies {
    implementation(project(":domain:book"))
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.kotlinx.serialization.json)
}
