package dev.kryptonreborn.bip.bip32.util

import com.ionspin.kotlin.bignum.integer.BigInteger
import dev.kryptonreborn.ecdsa.EcPoint
import dev.kryptonreborn.ecdsa.Secp256k1

object Secp256k1Util {
    /**
     * point(p): returns the coordinate pair resulting from EC point multiplication
     * (repeated application of the EC group operation) of the secp256k1 base point
     * with the integer p.
     *
     * @param p input
     * @return point
     */
    fun point(p: BigInteger): EcPoint {
        return Secp256k1.multiply(Secp256k1.g, p)
    }
}
