plugins {
    id("bookshelf.android.library")
    id("bookshelf.android.hilt")
}

android {
    namespace = "com.yangsooplus.bookshelf.data.datasource.network"
}

dependencies {
    api(libs.okhttp)
}
