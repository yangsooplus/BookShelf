plugins {
    id("bookshelf.android.application")
    id("bookshelf.android.compose")
    id("bookshelf.android.hilt")
}
android {
    namespace = "com.yangsooplus.bookshelf"
    defaultConfig {
        applicationId = "com.yangsooplus.bookshelf"
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
dependencies {
    implementation(project(":feature:book"))
    implementation(project(":data:book"))
    implementation(project(":core:designsystem"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
