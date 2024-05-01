package dev.kryptonreborn.bip.crypto

import org.kotlincrypto.core.digest.Digest
import org.kotlincrypto.hash.sha2.SHA512

object PBKDF2 {
    fun pbkdf2WithHmacSHA512(
        password: ByteArray,
        salt: ByteArray,
        iterationCount: Int,
        keySizeInBits: Int
    ): ByteArray = pbkdf2(password, salt, iterationCount, keySizeInBits, SHA512())

    private fun pbkdf2(
        password: ByteArray,
        salt: ByteArray,
        iterationCount: Int,
        keySizeInBits: Int,
        digest: Digest
    ): ByteArray {
        val hLen = digest.digest().size
        val blockSize = keySizeInBits / hLen
        val outSize = keySizeInBits / 8
        var offset = 0
        val result = ByteArray(outSize)
        val t = ByteArray(hLen)
        val i32be = ByteArray(4)
        val uv = ByteArray(salt.size + i32be.size)
        gen@ for (i in 1..blockSize) {
            t.fill(0)
            i.toByteArray(i32be)
            salt.copyInto(uv, 0, 0, salt.size)
            i32be.copyInto(uv, salt.size, 0, i32be.size)
            var u = uv
            for (c in 1..iterationCount) {
                u = hmac(password, u, digest)
                digest.reset()
                for (m in u.indices) {
                    t[m] = (t[m].toInt() xor u[m].toInt()).toByte()
                }
            }
            for (b in t) {
                result[offset++] = b
                if (offset >= outSize) {
                    break@gen
                }
            }
        }
        return result
    }

    private fun hmac(key: ByteArray, data: ByteArray, digest: Digest): ByteArray {
        var _key = key
        val blockSize = digest.blockSize()
        if (_key.size > blockSize) {
            digest.reset()
            digest.update(_key)
            _key = digest.digest()
        }
        if (_key.size < blockSize) {
            val newKey = ByteArray(blockSize)
            _key.copyInto(newKey, 0, 0, _key.size)
            _key = newKey
        }

        val oKeyPad = ByteArray(blockSize) { (0x5c xor _key[it].toInt()).toByte() }
        val iKeyPad = ByteArray(blockSize) { (0x36 xor _key[it].toInt()).toByte() }

        digest.reset()
        digest.update(iKeyPad)
        digest.update(data)
        val h1 = digest.digest()

        digest.reset()
        digest.update(oKeyPad)
        digest.update(h1)
        return digest.digest()
    }

    private fun Int.toByteArray(out: ByteArray = ByteArray(4)): ByteArray {
        out[0] = (this shr 24 and 0xff).toByte()
        out[1] = (this shr 16 and 0xff).toByte()
        out[2] = (this shr 8 and 0xff).toByte()
        out[3] = (this and 0xff).toByte()
        return out
    }
}
