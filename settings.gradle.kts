pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()

        fun mavenGithubRepo(repo: String) {
            maven {
                url = uri("https://maven.pkg.github.com/$repo")
                credentials {
                    username = System.getenv("USERNAME_GITHUB")
                    password = System.getenv("TOKEN_GITHUB")
                }
            }
        }

        mavenGithubRepo("KryptonReborn/kotlin-ed25519")
        mavenGithubRepo("KryptonReborn/kotlin-ecdsa")
        mavenGithubRepo("komputing/KHash")
    }
}

rootProject.name = "bip"

include(":bip32")
include(":bip39")
include(":bip44")
include(":crypto")
