package dev.kryptonreborn.bip.bip39

import org.kotlincrypto.core.digest.Digest
import org.kotlincrypto.hash.sha2.SHA512
import org.kotlincrypto.macs.hmac.sha2.HmacSHA512

class PBKDF2 {
    companion object {
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
                arraycopy(salt, 0, uv, 0, salt.size)
                arraycopy(i32be, 0, uv, salt.size, i32be.size)
                var u = uv
                for (c in 1..iterationCount) {
                    u = HmacSHA512(password).doFinal(u)
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

        private fun Int.toByteArray(out: ByteArray = ByteArray(4)): ByteArray {
            out[0] = (this shr 24 and 0xff).toByte()
            out[1] = (this shr 16 and 0xff).toByte()
            out[2] = (this shr 8 and 0xff).toByte()
            out[3] = (this and 0xff).toByte()
            return out
        }

        private fun arraycopy(
            src: ByteArray,
            srcPos: Int,
            dst: ByteArray,
            dstPos: Int,
            count: Int
        ) = src.copyInto(dst, dstPos, srcPos, srcPos + count)
    }
}
