plugins {
    id(libs.plugins.commonMppLib.get().pluginId)
    id(libs.plugins.commonMppPublish.get().pluginId)
}

publishConfig {
    url = "https://maven.pkg.github.com/KryptonReborn/kotlin-bip"
    groupId = "kotlin-bip"
    artifactId = "bip39"
}

version = "0.0.1"

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
