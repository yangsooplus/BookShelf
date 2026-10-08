plugins {
    id("bookshelf.android.library")
    id("bookshelf.android.hilt")
}

android {
    namespace = "com.yangsooplus.bookshelf.data.book"
}

dependencies {
    implementation(project(":domain:book"))
    implementation(project(":data:datasource:network"))
    implementation(project(":data:datasource:database"))
    implementation(libs.kotlinx.coroutines.core)
}
