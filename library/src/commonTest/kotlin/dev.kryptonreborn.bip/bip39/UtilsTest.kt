package dev.kryptonreborn.bip.bip39

import dev.kryptonreborn.bip.bip39.utils.fromHex
import dev.kryptonreborn.bip.bip39.utils.toHex
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class UtilsTest {
    @Test
    fun testConvertByteArrayToHexString() {
        val byteArray = byteArrayOf(-128, -16, 0, 16, 127)
        assertEquals("80f000107f", byteArray.toHex())
    }

    @Test
    fun testConvertHexStringToByteArray() {
        val hexString = "80f000107f"
        val expectedByteArray = byteArrayOf(-128, -16, 0, 16, 127)
        assertContentEquals(expectedByteArray, hexString.fromHex())
    }

    @Test
    fun testReturnOriginalValueToHexFromHex() {
        val originalBytes = ByteArray(256) { (it - 128).toByte() }
        val transformedBytes = originalBytes.toHex().fromHex()
        assertContentEquals(originalBytes, transformedBytes)
    }

    @Test
    fun testReturnOriginalValueFromHexToHex() {
        val originalHex = "0008101820283038404850586068707880889098a0a8b0b8c0c8d0d8e0e8f0f8"
        val transformedHex = originalHex.fromHex().toHex()
        assertEquals(originalHex, transformedHex)
    }
}
