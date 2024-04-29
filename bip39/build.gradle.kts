plugins {
    id(libs.plugins.commonMppLib.get().pluginId)
    id(libs.plugins.kotlinPluginSerialization.get().pluginId)
    id(libs.plugins.kotlinTestingResource.get().pluginId)
}

android {
    namespace = "dev.kryptonreborn.bip39"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinStdLib)
                implementation(libs.secureRandom)
                implementation(libs.kotlinCryptoHash)
                implementation(libs.kotlinCryptoMac)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlinxSerializationJson)
                implementation(libs.kotlinTestingResource)
            }
        }
    }
}
