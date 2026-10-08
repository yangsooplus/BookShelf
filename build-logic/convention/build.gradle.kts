plugins {
    `kotlin-dsl`
}
kotlin {
    jvmToolchain(17)
}
dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.compose.gradle.plugin)
    implementation(libs.hilt.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
}
gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "bookshelf.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "bookshelf.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "bookshelf.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "bookshelf.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "bookshelf.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidFeature") {
            id = "bookshelf.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
    }
}
