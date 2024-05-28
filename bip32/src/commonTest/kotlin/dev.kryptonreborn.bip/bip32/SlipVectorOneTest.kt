package dev.kryptonreborn.bip.bip32

import dev.kryptonreborn.bip.bip32.key.Curve
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@OptIn(ExperimentalStdlibApi::class)
class SlipVectorOneTest {
    companion object {
        val SEED: ByteArray = "000102030405060708090a0b0c0d0e0f".hexToByteArray()
    }

    @Test
    fun testVectorOneMaster() {
        val fingerprint: ByteArray = "00000000".hexToByteArray()
        val chainCode: ByteArray = "90046a93de5380a72b5e45010748567d5ea02bbf6522f979e05c0d8d8ca9fffb".hexToByteArray()
        val privateKey: ByteArray = "2b4be7f19ee27bbf30c667b642d5f4aa69fd169872f8fc3059c08ebae2eb19e7".hexToByteArray()
        val publicKey: ByteArray = "00a4b2856bfec510abab89753fac1ac0e1112364e7d250545963f135f2a33188ed".hexToByteArray()

        val address: HDAddress = HDKeyGenerator.getAddressFromSeed(SEED, Network.mainnet, CoinType.cardano)

        assertContentEquals(fingerprint, address.privateKey.fingerprint)
        assertContentEquals(chainCode, address.privateKey.chainCode)
        assertContentEquals(privateKey, address.privateKey.privateKey)
        assertContentEquals(publicKey, address.publicKey.publicKey)
        assertEquals(Curve.ed25519, address.coinType.curve)
    }

    @Test
    fun testVector0H() {
        val fingerprint: ByteArray = "ddebc675".hexToByteArray()
        val chainCode: ByteArray = "8b59aa11380b624e81507a27fedda59fea6d0b779a778918a2fd3590e16e9c69".hexToByteArray()
        val privateKey: ByteArray = "68e0fe46dfb67e368c75379acec591dad19df3cde26e63b93a8e704f1dade7a3".hexToByteArray()
        val publicKey: ByteArray = "008c8a13df77a28f3445213a0f432fde644acaa215fc72dcdf300d5efaa85d350c".hexToByteArray()

        val master: HDAddress = HDKeyGenerator.getAddressFromSeed(SEED, Network.mainnet, CoinType.cardano)
        val address: HDAddress = HDKeyGenerator.getAddress(master, 0, true)

        assertContentEquals(fingerprint, address.privateKey.fingerprint)
        assertContentEquals(chainCode, address.privateKey.chainCode)
        assertContentEquals(privateKey, address.privateKey.privateKey)
        assertContentEquals(publicKey, address.publicKey.publicKey)
        assertEquals(Curve.ed25519, address.coinType.curve)
    }

    @Test
    fun testVector0H1H() {
        val fingerprint: ByteArray = "13dab143".hexToByteArray()
        val chainCode: ByteArray = "a320425f77d1b5c2505a6b1b27382b37368ee640e3557c315416801243552f14".hexToByteArray()
        val privateKey: ByteArray = "b1d0bad404bf35da785a64ca1ac54b2617211d2777696fbffaf208f746ae84f2".hexToByteArray()
        val publicKey: ByteArray = "001932a5270f335bed617d5b935c80aedb1a35bd9fc1e31acafd5372c30f5c1187".hexToByteArray()

        val master: HDAddress = HDKeyGenerator.getAddressFromSeed(SEED, Network.mainnet, CoinType.cardano)
        var address: HDAddress = HDKeyGenerator.getAddress(master, 0, true)
        address = HDKeyGenerator.getAddress(address, 1, true)

        assertContentEquals(fingerprint, address.privateKey.fingerprint)
        assertContentEquals(chainCode, address.privateKey.chainCode)
        assertContentEquals(privateKey, address.privateKey.privateKey)
        assertContentEquals(publicKey, address.publicKey.publicKey)
        assertEquals(Curve.ed25519, address.coinType.curve)
    }

    @Test
    fun testVector0H1H2H() {
        val fingerprint: ByteArray = "ebe4cb29".hexToByteArray()
        val chainCode: ByteArray = "2e69929e00b5ab250f49c3fb1c12f252de4fed2c1db88387094a0f8c4c9ccd6c".hexToByteArray()
        val privateKey: ByteArray = "92a5b23c0b8a99e37d07df3fb9966917f5d06e02ddbd909c7e184371463e9fc9".hexToByteArray()
        val publicKey: ByteArray = "00ae98736566d30ed0e9d2f4486a64bc95740d89c7db33f52121f8ea8f76ff0fc1".hexToByteArray()

        val master: HDAddress = HDKeyGenerator.getAddressFromSeed(SEED, Network.mainnet, CoinType.cardano)
        var address: HDAddress = HDKeyGenerator.getAddress(master, 0, true)
        address = HDKeyGenerator.getAddress(address, 1, true)
        address = HDKeyGenerator.getAddress(address, 2, true)

        assertContentEquals(fingerprint, address.privateKey.fingerprint)
        assertContentEquals(chainCode, address.privateKey.chainCode)
        assertContentEquals(privateKey, address.privateKey.privateKey)
        assertContentEquals(publicKey, address.publicKey.publicKey)
        assertEquals(Curve.ed25519, address.coinType.curve)
    }
}
