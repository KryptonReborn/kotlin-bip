package plugins

import com.android.build.gradle.LibraryExtension
import extensions.libs
import org.gradle.api.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

class CommonMppLibPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.findPlugin("androidLibrary").get().get().pluginId)
                apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
                apply(libs.findPlugin("kotlinPluginSerialization").get().get().pluginId)
                apply(libs.findPlugin("kotlinTestingResource").get().get().pluginId)
            }

            extensions.configure<LibraryExtension> {
                compileSdk = libs.findVersion("androidCompileSdk").get().displayName.toInt()
                defaultConfig {
                    minSdk = libs.findVersion("androidMinSdk").get().displayName.toInt()
                }
            }

            extensions.configure<KotlinMultiplatformExtension> {
                val hostOs = getHostOsName()
                println("Host os name $hostOs")

                when (hostOs) {
                    HostOs.LINUX -> linuxX64("native")
                    HostOs.MAC -> macosX64("native")
                    HostOs.WINDOWS -> mingwX64("native")
                }
                jvm()
                js().apply {
                    compilations.apply {
                        nodejs {
                            testTask {
                                useMocha {
                                    timeout = "20s"
                                }
                            }
                        }
                        browser {
                            testTask {
                                enabled = false
                            }
                        }
                    }
                }
// waiting for wasm support in kotlinx resource https://github.com/goncalossilva/kotlinx-resources/issues/91
//                @OptIn(ExperimentalWasmDsl::class)
//                wasmJs {
//                    browser()
//                    binaries.executable()
//                }
                androidTarget {
                    publishLibraryVariants("release")
                    compilations.all {
                        kotlinOptions {
                            jvmTarget = "1.8"
                        }
                    }
                }
                iosX64("ios")
                iosArm64()
                iosSimulatorArm64()

                sourceSets.apply {
                    commonMain.get()
                    commonTest.dependencies {
                        implementation(libs.findLibrary("kotlinTest").get())
                        implementation(libs.findLibrary("kotlinxSerializationJson").get())
                        implementation(libs.findLibrary("kotlinTestingResource").get())
                    }
                    nativeMain.get().dependsOn(commonMain.get())
                    nativeTest.get().dependsOn(commonTest.get())
                    iosMain.get().dependsOn(nativeMain.get())
                    iosTest.get().dependsOn(nativeTest.get())
                }
            }
        }
    }

    enum class HostOs {
        LINUX, WINDOWS, MAC
    }

    private fun getHostOsName(): HostOs {
        val target = System.getProperty("os.name")
        return when {
            target == "Linux" -> HostOs.LINUX
            target.startsWith("Windows") -> HostOs.WINDOWS
            target.startsWith("Mac") -> HostOs.MAC
            else -> throw GradleException("Unknown OS: $target")
        }
    }
}
