package dev.kryptonreborn.bip.bip32.key

data class HDPublicKey(
    override val version: ByteArray = byteArrayOf(),
    override val depth: Int = 0,
    override var fingerprint: ByteArray = byteArrayOf(),
    override val childNumber: ByteArray = byteArrayOf(),
    override val chainCode: ByteArray = byteArrayOf(),
    override val keyData: ByteArray = byteArrayOf(),
    var publicKey: ByteArray = byteArrayOf()
) : HDKey(version, depth, fingerprint, childNumber, chainCode, keyData)
