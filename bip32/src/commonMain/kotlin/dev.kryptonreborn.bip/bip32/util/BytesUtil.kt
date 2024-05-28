package dev.kryptonreborn.bip.bip32.util

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign

@OptIn(ExperimentalStdlibApi::class)
object BytesUtil {
    /**
     * ser32(i): serialize a 32-bit unsigned integer i as a 4-byte sequence, most
     * significant byte first.
     *
     * Prefer long type to hold unsigned ints.
     *
     * @return ser32(i)
     */
    fun ser32(i: Long): ByteArray {
        val ser = ByteArray(4)
        ser[0] = (i shr 24).toByte()
        ser[1] = (i shr 16).toByte()
        ser[2] = (i shr 8).toByte()
        ser[3] = i.toByte()
        return ser
    }

    /**
     * ser256(p): serializes the integer p as a 32-byte sequence, most significant
     * byte first.
     *
     * @param p big integer
     * @return 32 byte sequence
     */
    fun ser256(p: BigInteger): ByteArray {
        val byteArray = p.toString(16).hexToByteArray()
        val ret = ByteArray(32)

        // 0 fill value
        ret.fill(0.toByte())

        // copy the bigint in
        if (byteArray.size <= ret.size) {
            byteArray.copyInto(ret, ret.size - byteArray.size, 0, byteArray.size)
        } else {
            byteArray.copyInto(ret, 0, byteArray.size - ret.size, byteArray.size)
        }
        return ret
    }

    /**
     * parse256(p): interprets a 32-byte sequence as a 256-bit number, most
     * significant byte first.
     *
     * @param p bytes
     * @return 256 bit number
     */
    fun parse256(p: ByteArray): BigInteger {
        return BigInteger.fromByteArray(p, Sign.POSITIVE)
    }
}
