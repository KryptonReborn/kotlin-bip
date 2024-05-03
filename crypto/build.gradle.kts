plugins {
    id(libs.plugins.commonMppLib.get().pluginId)
    id(libs.plugins.commonMppPublish.get().pluginId)
}

publishConfig {
    url = "https://maven.pkg.github.com/KryptonReborn/kotlin-bip"
    groupId = "kotlin-bip"
    artifactId = "crypto"
}

version = "0.0.1"

android {
    namespace = "dev.kryptonreborn.bip.crypto"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinCryptoHash)
            }
        }
    }
}
