package dev.kryptonreborn.bip.bip32

import dev.kryptonreborn.bip.bip32.key.HDPrivateKey
import dev.kryptonreborn.bip.bip32.key.HDPublicKey

/**
 * An HD public/private key
 */
data class HDAddress(
    val privateKey: HDPrivateKey,
    val publicKey: HDPublicKey,
    val coinType: CoinType,
    val path: String
)
