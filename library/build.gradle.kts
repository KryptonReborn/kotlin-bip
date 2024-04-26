plugins {
    id(libs.plugins.commonMppLib.get().pluginId)
}

android {
    namespace = "dev.kryptonreborn.bip"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinStdLib)
                implementation(libs.krypto)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotestFrameworkEngine)
                implementation(libs.kotestAssertion)
                implementation(libs.kotestProperty)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotestRunnerJunit5)
            }
        }
    }
}
