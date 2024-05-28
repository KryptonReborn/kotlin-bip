package dev.kryptonreborn.bip.bip32

import dev.kryptonreborn.bip.bip32.key.Curve

enum class CoinType(
    val curve: Curve,
    val coinType: Long,
    val alwaysHardened: Boolean
) {
    bitcoin(Curve.bitcoin, 0, false),
    cardano(Curve.ed25519, 1815, true);
}
