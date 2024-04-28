package dev.kryptonreborn.bip.bip39

import korlibs.crypto.SecureRandom

/**
 * The supported word counts that can be used for creating entropy.
 *
 * @param count the number of words in the resulting mnemonic
 */
enum class WordCount(val count: Int) {
    COUNT_12(12),
    COUNT_15(15),
    COUNT_18(18),
    COUNT_21(21),
    COUNT_24(24);

    /**
     * The bit length of the entropy necessary to create a mnemonic with the given word count.
     */
    val bitLength = count / 3 * 32

    companion object {
        /**
         * Convert a count into an instance of [WordCount].
         */
        fun valueOf(count: Int): WordCount? {
            values().forEach {
                if (it.count == count) return it
            }
            return null
        }
    }

    fun toEntropy(): ByteArray = ByteArray(bitLength / 8).apply {
        SecureRandom.nextBytes(this)
    }
}
