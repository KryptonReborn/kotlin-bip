package dev.kryptonreborn.bip.bip32

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test vector from https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki
 */
@OptIn(ExperimentalStdlibApi::class)
class VectorTwoTest : BaseVectorTest() {
    override val seed: ByteArray
        get() = "fffcf9f6f3f0edeae7e4e1dedbd8d5d2cfccc9c6c3c0bdbab7b4b1aeaba8a5a29f9c999693908d8a8784817e7b7875726f6c696663605d5a5754514e4b484542".hexToByteArray()

    @Test
    fun testMasterNodePrivateKey() {
        val expected =
            "xprv9s21ZrQH143K31xYSDQpPDxsXRTUcvj2iNHm5NUtrGiGG5e2DtALGdso3pGz6ssrdK4PFmM8NSpSBHNqPqm55Qn3LqFtT2emdEXVYsCzC2U"
        assertEquals(expected, Base58.encode(masterNode.privateKey.getKey()))
    }

    @Test
    fun testMasterNodePublicKey() {
        val expected =
            "xpub661MyMwAqRbcFW31YEwpkMuc5THy2PSt5bDMsktWQcFF8syAmRUapSCGu8ED9W6oDMSgv6Zz8idoc4a6mr8BDzTJY47LJhkJ8UB7WEGuduB"
        assertEquals(expected, Base58.encode(masterNode.publicKey.getKey()))
    }

    @Test
    fun testChain0HPrivateKey() {
        val expected =
            "xprv9vHkqa6EV4sPZHYqZznhT2NPtPCjKuDKGY38FBWLvgaDx45zo9WQRUT3dKYnjwih2yJD9mkrocEZXo1ex8G81dwSM1fwqWpWkeS3v86pgKt"
        val chain: HDAddress = HDKeyGenerator.getAddress(masterNode, 0, false)
        assertEquals(expected, Base58.encode(chain.privateKey.getKey()))
    }

    @Test
    fun testChain0HPublicKey() {
        val expected =
            "xpub69H7F5d8KSRgmmdJg2KhpAK8SR3DjMwAdkxj3ZuxV27CprR9LgpeyGmXUbC6wb7ERfvrnKZjXoUmmDznezpbZb7ap6r1D3tgFxHmwMkQTPH"
        val chain: HDAddress = HDKeyGenerator.getAddress(masterNode, 0, false)
        assertEquals(expected, Base58.encode(chain.publicKey.getKey()))
    }

    @Test
    fun testChain0_2147483647HPrivateKey() {
        val expected =
            "xprv9wSp6B7kry3Vj9m1zSnLvN3xH8RdsPP1Mh7fAaR7aRLcQMKTR2vidYEeEg2mUCTAwCd6vnxVrcjfy2kRgVsFawNzmjuHc2YmYRmagcEPdU9"
        var chain: HDAddress = HDKeyGenerator.getAddress(masterNode, 0, false)
        chain = HDKeyGenerator.getAddress(chain, 2147483647, true)
        assertEquals(expected, Base58.encode(chain.privateKey.getKey()))
    }
}
