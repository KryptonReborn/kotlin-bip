package dev.kryptonreborn.bip.bip32.key

data class HDPrivateKey(
    override val version: ByteArray = byteArrayOf(),
    override val depth: Int = 0,
    override var fingerprint: ByteArray = byteArrayOf(),
    override val childNumber: ByteArray = byteArrayOf(),
    override var chainCode: ByteArray = byteArrayOf(),
    override var keyData: ByteArray = byteArrayOf(),
    var privateKey: ByteArray = byteArrayOf()
) : HDKey(version, depth, fingerprint, childNumber, chainCode, keyData)
