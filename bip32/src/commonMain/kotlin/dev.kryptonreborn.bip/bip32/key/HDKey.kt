package dev.kryptonreborn.bip.bip32.key

import dev.kryptonreborn.bip.bip32.util.HashUtil.sha256Twice
import kotlinx.io.Buffer
import kotlinx.io.readByteArray

sealed class HDKey(
    open val version: ByteArray,
    open val depth: Int,
    open val fingerprint: ByteArray,
    open val childNumber: ByteArray,
    open val chainCode: ByteArray,
    open val keyData: ByteArray,
) {
    /**
     * Get the full chain key. This is not the public/private key for the address.
     * @return full HD Key
     */
    fun getKey(): ByteArray {
        val buffer = Buffer().apply {
            write(version)
            write(byteArrayOf(depth.toByte()))
            write(fingerprint)
            write(childNumber)
            write(chainCode)
            write(keyData)
        }
        val key = buffer.copy()
        val checksum: ByteArray = sha256Twice(buffer.readByteArray())
        key.write(checksum.copyOfRange(0, 4))

        return key.readByteArray()
    }
}
