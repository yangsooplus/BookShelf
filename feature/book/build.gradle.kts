plugins {
    id("bookshelf.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.yangsooplus.bookshelf.feature.book"
}

dependencies {
    implementation(project(":domain:book"))
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.compose.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.json)
}
