package dev.kryptonreborn.bip.bip32.key

enum class Curve(val seed: String) {
    bitcoin("Bitcoin seed"),
    ed25519("ed25519 seed")
}
