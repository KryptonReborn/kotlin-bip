plugins {
    id(libs.plugins.commonMppLib.get().pluginId)
}

android {
    namespace = "dev.kryptonreborn.bip.bip44"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {}
        }
    }
}
