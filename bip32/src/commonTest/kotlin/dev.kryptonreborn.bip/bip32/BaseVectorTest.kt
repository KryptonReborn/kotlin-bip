package dev.kryptonreborn.bip.bip32

abstract class BaseVectorTest {
    val masterNode: HDAddress

    init {
        masterNode = HDKeyGenerator.getAddressFromSeed(seed, Network.mainnet, CoinType.bitcoin)
    }

    protected abstract val seed: ByteArray
}
