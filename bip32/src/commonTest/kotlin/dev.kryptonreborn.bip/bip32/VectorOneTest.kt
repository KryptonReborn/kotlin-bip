package dev.kryptonreborn.bip.bip32

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test vector from https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki
 */
@OptIn(ExperimentalStdlibApi::class)
class VectorOneTest : BaseVectorTest() {
    override val seed: ByteArray
        get() = "000102030405060708090a0b0c0d0e0f".hexToByteArray()

    @Test
    fun testMasterNodePrivateKey() {
        val expected =
            "xprv9s21ZrQH143K3QTDL4LXw2F7HEK3wJUD2nW2nRk4stbPy6cq3jPPqjiChkVvvNKmPGJxWUtg6LnF5kejMRNNU3TGtRBeJgk33yuGBxrMPHi"
        assertEquals(expected, Base58.encode(masterNode.privateKey.getKey()))
    }

    @Test
    fun testMasterNodePublicKey() {
        val expected =
            "xpub661MyMwAqRbcFtXgS5sYJABqqG9YLmC4Q1Rdap9gSE8NqtwybGhePY2gZ29ESFjqJoCu1Rupje8YtGqsefD265TMg7usUDFdp6W1EGMcet8"
        assertEquals(expected, Base58.encode(masterNode.publicKey.getKey()))
    }

    @Test
    fun testChain0HPrivateKey() {
        //this is hardened
        val expected =
            "xprv9uHRZZhk6KAJC1avXpDAp4MDc3sQKNxDiPvvkX8Br5ngLNv1TxvUxt4cV1rGL5hj6KCesnDYUhd7oWgT11eZG7XnxHrnYeSvkzY7d2bhkJ7"
        val chain: HDAddress = HDKeyGenerator.getAddress(masterNode, 0, true)
        assertEquals(expected, Base58.encode(chain.privateKey.getKey()))
    }

    @Test
    fun testChain0HPublicKey() {
        val expected =
            "xpub68Gmy5EdvgibQVfPdqkBBCHxA5htiqg55crXYuXoQRKfDBFA1WEjWgP6LHhwBZeNK1VTsfTFUHCdrfp1bgwQ9xv5ski8PX9rL2dZXvgGDnw"
        val chain: HDAddress = HDKeyGenerator.getAddress(masterNode, 0, true)
        assertEquals(expected, Base58.encode(chain.publicKey.getKey()))
    }

    @Test
    fun testChain0H1PrivateKey() {
        val expected =
            "xprv9wTYmMFdV23N2TdNG573QoEsfRrWKQgWeibmLntzniatZvR9BmLnvSxqu53Kw1UmYPxLgboyZQaXwTCg8MSY3H2EU4pWcQDnRnrVA1xe8fs"
        var chain: HDAddress = HDKeyGenerator.getAddress(masterNode, 0, true)
        chain = HDKeyGenerator.getAddress(chain, 1, false)
        assertEquals(expected, Base58.encode(chain.privateKey.getKey()))
    }

    @Test
    fun testChain0H1PublicKey() {
        val expected =
            "xpub6ASuArnXKPbfEwhqN6e3mwBcDTgzisQN1wXN9BJcM47sSikHjJf3UFHKkNAWbWMiGj7Wf5uMash7SyYq527Hqck2AxYysAA7xmALppuCkwQ"
        var chain: HDAddress = HDKeyGenerator.getAddress(masterNode, 0, true)
        chain = HDKeyGenerator.getAddress(chain, 1, false)
        assertEquals(expected, Base58.encode(chain.publicKey.getKey()))
    }

    @Test
    fun testChain0H12HPrivateKey() {
        val expected =
            "xprv9z4pot5VBttmtdRTWfWQmoH1taj2axGVzFqSb8C9xaxKymcFzXBDptWmT7FwuEzG3ryjH4ktypQSAewRiNMjANTtpgP4mLTj34bhnZX7UiM"
        var chain: HDAddress = HDKeyGenerator.getAddress(masterNode, 0, true)
        chain = HDKeyGenerator.getAddress(chain, 1, false)
        chain = HDKeyGenerator.getAddress(chain, 2, true)
        assertEquals(expected, Base58.encode(chain.privateKey.getKey()))
    }

    @Test
    fun testChain0H12HPublicKey() {
        val expected =
            "xpub6D4BDPcP2GT577Vvch3R8wDkScZWzQzMMUm3PWbmWvVJrZwQY4VUNgqFJPMM3No2dFDFGTsxxpG5uJh7n7epu4trkrX7x7DogT5Uv6fcLW5"
        var chain: HDAddress = HDKeyGenerator.getAddress(masterNode, 0, true)
        chain = HDKeyGenerator.getAddress(chain, 1, false)
        chain = HDKeyGenerator.getAddress(chain, 2, true)
        assertEquals(expected, Base58.encode(chain.publicKey.getKey()))
    }
}
