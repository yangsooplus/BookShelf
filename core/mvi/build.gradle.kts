plugins {
    id("bookshelf.android.library")
}

android {
    namespace = "com.yangsooplus.bookshelf.core.mvi"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.kotlinx.coroutines.core)
}
