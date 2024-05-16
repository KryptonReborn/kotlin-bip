package dev.kryptonreborn.bip.bip32

/**
 * Defined network values for key generation
 */
@OptIn(ExperimentalStdlibApi::class)
enum class Network(privatePrefix: String, publicPrefix: String) {
    mainnet("0x0488ADE4", "0x0488B21E"),
    testnet("0x04358394", "0x043587CF");

    val privateKeyVersion: ByteArray = privatePrefix.decodeHex()
    val publicKeyVersion: ByteArray = publicPrefix.decodeHex()

    private fun String.decodeHex(): ByteArray = this.substring(2).hexToByteArray()
}
