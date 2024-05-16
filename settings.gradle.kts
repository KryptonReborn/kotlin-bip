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
                url = uri("https://maven.pkg.github.com/KryptonReborn/$repo")
                credentials {
                    username = System.getenv("USERNAME_GITHUB")
                    password = System.getenv("TOKEN_GITHUB")
                }
            }
        }

        mavenGithubRepo("kotlin-ed25519")
        mavenGithubRepo("kotlin-bech32")
        mavenGithubRepo("kotlin-ecdsa")

        maven {
            name = "komputing/KHash GitHub Packages"
            url = uri("https://maven.pkg.github.com/komputing/KHash")
            credentials {
                username = "token"
                password = System.getenv("KOMPUTING_KHASH_TOKEN")
            }
        }
    }
}

rootProject.name = "bip"

include(":bip32")
include(":bip39")
include(":bip44")
include(":crypto")
