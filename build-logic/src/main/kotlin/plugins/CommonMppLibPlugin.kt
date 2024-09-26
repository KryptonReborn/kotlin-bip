package plugins

import com.android.build.gradle.LibraryExtension
import extensions.libs
import extensions.registerPrintLineCoverageTask
import org.gradle.api.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsSubTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

class CommonMppLibPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.findPlugin("androidLibrary").get().get().pluginId)
                apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
                apply(libs.findPlugin("kotlinPluginSerialization").get().get().pluginId)
                apply(libs.findPlugin("kotlinTestingResource").get().get().pluginId)
                apply(libs.findPlugin("dokka").get().get().pluginId)
                apply(libs.findPlugin("kover").get().get().pluginId)
            }

            registerPrintLineCoverageTask()

            extensions.configure<LibraryExtension> {
                compileSdk = libs.findVersion("androidCompileSdk").get().displayName.toInt()
                defaultConfig {
                    minSdk = libs.findVersion("androidMinSdk").get().displayName.toInt()
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
            }

            extensions.configure<KotlinMultiplatformExtension> {
                jvm {
                    compilations.all {
                        kotlinOptions.jvmTarget = "17"
                    }
                    testRuns["test"].executionTask.configure {
                        useJUnitPlatform()
                    }
                }
                androidTarget {
                    publishAllLibraryVariants()
                    publishLibraryVariantsGroupedByFlavor = true
                    compilations.all {
                        kotlinOptions.jvmTarget = "17"
                    }
                }

                js {
                    configureTargetsForJS()
                }
// waiting for wasm support in kotlinx resource https://github.com/goncalossilva/kotlinx-resources/issues/91
//                @OptIn(ExperimentalWasmDsl::class)
//                wasmJs {
//                    configureTargetsForJS()
//                }
//                @OptIn(ExperimentalWasmDsl::class)
//                wasmWasi {
//                    nodejs()
//                }
                iosArm64()
                iosX64()
                iosSimulatorArm64()

                mingwX64()
                macosX64()
                macosArm64()
                linuxX64()
                linuxArm64()

                applyDefaultHierarchyTemplate()

                sourceSets.apply {
                    commonMain.get()
                    commonTest.get().dependencies {
                        implementation(libs.findLibrary("kotlinTest").get())
                        implementation(libs.findLibrary("kotlinxSerializationJson").get())
                        implementation(libs.findLibrary("kotlinTestingResource").get())
                    }
                }
            }
        }
    }

    private fun KotlinJsSubTargetDsl.configureMochaTimeout() {
        testTask {
            useMocha {
                timeout = "20s"
            }
        }
    }

    private fun KotlinJsTargetDsl.configureTargetsForJS() {
        browser {
            configureMochaTimeout()
        }
        nodejs {
            configureMochaTimeout()
        }
        binaries.executable()
    }
}
