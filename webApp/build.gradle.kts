import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.gradle.api.tasks.Copy

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

tasks.withType<Copy>().configureEach {
    if (name == "wasmJsProcessResources" || name == "jsProcessResources") {
        from(rootProject.layout.projectDirectory.dir("blog")) {
            into("blog")
            exclude("README.md")
        }
    }
}

kotlin {
    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared"))

            implementation(libs.compose.ui)
            implementation(libs.wrappers.browser)
        }
    }
}
