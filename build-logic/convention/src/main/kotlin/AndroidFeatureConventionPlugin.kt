import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        pluginManager.apply("bookshelf.android.library")
        pluginManager.apply("bookshelf.android.compose")
        pluginManager.apply("bookshelf.android.hilt")
        dependencies.add("implementation", project(":core:mvi"))
        dependencies.add("implementation", project(":core:designsystem"))
        dependencies.add("implementation", library("androidx-lifecycle-viewmodel-ktx"))
        dependencies.add("implementation", library("kotlinx-coroutines-core"))
    }
}
