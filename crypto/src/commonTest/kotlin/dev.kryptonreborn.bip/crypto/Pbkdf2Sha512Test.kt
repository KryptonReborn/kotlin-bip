package dev.kryptonreborn.bip.crypto

import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalStdlibApi::class)
class Pbkdf2Sha512Test {
    private val pbkdf2TestData = loadPbkdf2TestDataJson()

    @Test
    fun testPbkdf2WithHmacSHA512() {
        pbkdf2TestData.forEach {
            val result =
                PBKDF2.pbkdf2WithHmacSHA512(
                    it.password.encodeToByteArray(),
                    it.salt.encodeToByteArray(),
                    it.count,
                    it.length,
                )
            assertEquals(it.expected, result.toHexString())
        }
    }
}
