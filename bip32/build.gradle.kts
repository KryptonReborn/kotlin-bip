import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    id(libs.plugins.commonMppLib.get().pluginId)
}

android {
    namespace = "dev.kryptonreborn.bip.bip32"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinCryptoHash)
                implementation(libs.kotlinBignum)
                implementation(libs.kotlinxIo)
                implementation(libs.ed25519)
                implementation(libs.ecdsa)
                implementation(libs.ripemd160)
                implementation(project(BuildModules.CRYPTO))
                implementation(project(BuildModules.BIP39))
                implementation(project(BuildModules.BIP44))
            }
        }
    }
}

rootProject.plugins.withType<YarnPlugin> {
    rootProject.configure<YarnRootExtension> {
        yarnLockMismatchReport = YarnLockMismatchReport.WARNING
        yarnLockAutoReplace = true
    }
}
