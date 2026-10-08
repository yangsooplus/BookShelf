plugins {
    id("bookshelf.android.feature")
}

android {
    namespace = "com.yangsooplus.bookshelf.feature.book"
}

dependencies {
    implementation(project(":domain:book"))
}
