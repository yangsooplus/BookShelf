import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
        pluginManager.withPlugin("com.android.application") {
            extensions.configure<ApplicationExtension> { buildFeatures.compose = true }
        }
        pluginManager.withPlugin("com.android.library") {
            extensions.configure<LibraryExtension> { buildFeatures.compose = true }
        }
        dependencies.apply {
            add("implementation", platform(library("androidx-compose-bom")))
            add("implementation", library("androidx-compose-ui"))
            add("implementation", library("androidx-compose-ui-tooling-preview"))
            add("implementation", library("androidx-compose-material3"))
            add("debugImplementation", library("androidx-compose-ui-tooling"))
            add("androidTestImplementation", platform(library("androidx-compose-bom")))
            add("androidTestImplementation", library("androidx-compose-ui-test-junit4"))
            add("debugImplementation", library("androidx-compose-ui-test-manifest"))
        }
    }
}
