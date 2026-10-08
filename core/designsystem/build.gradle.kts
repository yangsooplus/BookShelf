plugins {
    id("bookshelf.android.library")
    id("bookshelf.android.compose")
}

android {
    namespace = "com.yangsooplus.bookshelf.core.designsystem"
}

dependencies {
    api(libs.androidx.compose.material3)
}
