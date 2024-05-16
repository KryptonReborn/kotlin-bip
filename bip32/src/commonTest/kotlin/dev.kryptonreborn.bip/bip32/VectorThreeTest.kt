package dev.kryptonreborn.bip.bip32

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test vector from https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki
 */
@OptIn(ExperimentalStdlibApi::class)
class VectorThreeTest : BaseVectorTest() {
    override val seed: ByteArray
        get() = "4b381541583be4423346c643850da4b320e46a87ae3d2a4e6da11eba819cd4acba45d239319ac14f863b8d5ab5a0d0c64d2e8a1e7d1457df2e5a3c51c73235be".hexToByteArray()

    @Test
    fun testMasterNodePrivateKey() {
        val expected =
            "xprv9s21ZrQH143K25QhxbucbDDuQ4naNntJRi4KUfWT7xo4EKsHt2QJDu7KXp1A3u7Bi1j8ph3EGsZ9Xvz9dGuVrtHHs7pXeTzjuxBrCmmhgC6"
        assertEquals(expected, Base58.encode(masterNode.privateKey.getKey()))
    }

    @Test
    fun testMasterNodePublicKey() {
        val expected =
            "xpub661MyMwAqRbcEZVB4dScxMAdx6d4nFc9nvyvH3v4gJL378CSRZiYmhRoP7mBy6gSPSCYk6SzXPTf3ND1cZAceL7SfJ1Z3GC8vBgp2epUt13"
        assertEquals(expected, Base58.encode(masterNode.publicKey.getKey()))
    }

    @Test
    fun testChain0HPrivateKey() {
        val expected =
            "xprv9uPDJpEQgRQfDcW7BkF7eTya6RPxXeJCqCJGHuCJ4GiRVLzkTXBAJMu2qaMWPrS7AANYqdq6vcBcBUdJCVVFceUvJFjaPdGZ2y9WACViL4L"
        val chain: HDAddress = HDKeyGenerator.getAddress(masterNode, 0, true)
        assertEquals(expected, Base58.encode(chain.privateKey.getKey()))
    }
}
