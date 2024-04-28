plugins {
    id(libs.plugins.commonMppLib.get().pluginId)
    id(libs.plugins.kotlinPluginSerialization.get().pluginId)
    id(libs.plugins.kotlinTestingResource.get().pluginId)
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
                implementation(libs.kotlinxSerializationJson)
                implementation(libs.kotlinTestingResource)
            }
        }
    }
}
