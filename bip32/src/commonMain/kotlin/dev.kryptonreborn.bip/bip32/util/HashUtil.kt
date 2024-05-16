package dev.kryptonreborn.bip.bip32.util

import org.komputing.khash.ripemd160.Ripemd160Digest
import org.kotlincrypto.hash.sha2.SHA256

object HashUtil {
    /**
     * sha256(sha256(bytes))
     *
     * @param bytes input
     * @return sha twice result
     */
    fun sha256Twice(bytes: ByteArray): ByteArray {
        return sha256Twice(bytes, 0, bytes.size)
    }

    fun sha256Twice(bytes: ByteArray, offset: Int, length: Int): ByteArray {
        val digest = SHA256()
        digest.update(bytes, offset, length)
        digest.update(digest.digest())
        return digest.digest()
    }

    /**
     * H160
     *
     * @param input input
     * @return h160(input)
     */
    fun h160(input: ByteArray): ByteArray {
        val sha256: ByteArray = SHA256().digest(input)

        val digest = Ripemd160Digest()
        digest.update(sha256, 0, sha256.size)
        val out = ByteArray(20)
        digest.doFinal(out, 0)
        return out
    }
}
