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
                    username = "token"
                    password = "92741d83d69acaf1b4a0453ac2686b625e4ae42b"
                }
            }
        }

        mavenGithubRepo("kotlin-ed25519")
        mavenGithubRepo("kotlin-ecdsa")

        maven {
            name = "komputing/KHash GitHub Packages"
            url = uri("https://maven.pkg.github.com/komputing/KHash")
            credentials {
                username = "token"
                password = "92741d83d69acaf1b4a0453ac2686b625e4ae42b"
            }
        }
    }
}

rootProject.name = "bip"

include(":bip32")
include(":bip39")
include(":bip44")
include(":crypto")
