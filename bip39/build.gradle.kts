plugins {
    id(libs.plugins.commonMppLib.get().pluginId)
}

android {
    namespace = "dev.kryptonreborn.bip.bip39"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.secureRandom)
                implementation(libs.kotlinCryptoHash)
                implementation(project(BuildModules.CRYPTO))
            }
        }
    }
}
