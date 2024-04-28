package dev.kryptonreborn.bip.bip39

import dev.kryptonreborn.bip.bip39.MnemonicException.*
import dev.kryptonreborn.bip.bip39.utils.*
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MnemonicTest {
    private val validPhrase =
        "void come effort suffer camp survey warrior heavy shoot primary clutch crush open amazing" +
                " screen patrol group space point ten exist slush involve unfold"

    // Uses test values from the original BIP : https://github.com/trezor/python-mnemonic/blob/master/vectors.json
    private val englishTestData = loadTestDataJson()

    @Test
    fun testConvertValidMnemonicToSeed() {
        val result = Mnemonic(validPhrase).toSeed()
        val hex = result.toHex()
        assertEquals(64, result.size)
        assertEquals(128, hex.length)
        assertEquals(
            "b873212f885ccffbf4692afcb84bc2e55886de2dfa07d90f5c3c239abc31c0a6ce047e30fd8bf6a281e71389aa82d73df74c7bbfb3b06b4639a5cee775cccd3c",
            hex
        )
    }

    @Test
    fun testWordCountBitLength() {
        val listTest = listOf(Pair(12, 128), Pair(15, 160), Pair(18, 192), Pair(21, 224), Pair(24, 256))
        listTest.forEach {
            val wordCount = WordCount.valueOf(it.first)
            val entropy = wordCount!!.toEntropy()
            assertEquals(it.second, wordCount.bitLength)
            assertEquals(it.second, entropy.size * 8)
        }
    }

    @Test
    fun testMnemonicFromWordCount() {
        WordCount.values().forEach { wordCount ->
            Mnemonic(wordCount).let { mnemonic ->
                val mnemonicPhrase = mnemonic.chars.concatToString()
                val words = mnemonic.words.map { it.concatToString() }
                assertEquals(wordCount.count - 1, mnemonic.chars.count { it == ' ' })
                assertEquals(wordCount.count, words.size)
                assertContentEquals(words, mnemonicPhrase.split(' '))
            }
        }
    }

    @Test
    fun testConvertEntropyToMnemonic() {
        englishTestData.forEach {
            assertEquals(it.mnemonic, Mnemonic(it.entropy.fromHex()).chars.concatToString())
        }
    }

    @Test
    fun testConvertMnemonicToEntropy() {
        englishTestData.forEach {
            assertEquals(it.entropy, Mnemonic(it.mnemonic).toEntropy().toHex())
        }
    }

    @Test
    fun testConvertMnemonicToSeed() {
        val passphrase = "TREZOR".toCharArray()
        englishTestData.forEach {
            assertEquals(it.seed, Mnemonic(it.mnemonic, Mnemonic.DEFAULT_LANGUAGE_CODE).toSeed(passphrase).toHex())
        }
    }

    @Test
    fun testInvalidMnemonicBySwap() {
        // swapped "trend" and "flight"
        val mnemonicPhrase = validPhrase.swap(4, 5)
        assertFailsWith<ChecksumException> {
            Mnemonic(mnemonicPhrase).validate()
        }
        assertFailsWith<ChecksumException> {
            Mnemonic(mnemonicPhrase).toEntropy()
        }
        assertFailsWith<ChecksumException> {
            Mnemonic(mnemonicPhrase).toSeed()
        }
        // toSeed(validate=false) succeeds!!
        assertEquals(64, Mnemonic(mnemonicPhrase).toSeed(validate = false).size)
    }

    @Test
    fun testInvalidMnemonicWithInvalidWord() {
        val mnemonicPhrase =
            validPhrase.split(' ').let { words ->
                validPhrase.replace(words[23], "convincee")
            }
        assertFailsWith<InvalidWordException> {
            Mnemonic(mnemonicPhrase).validate()
        }
        assertFailsWith<InvalidWordException> {
            Mnemonic(mnemonicPhrase).toEntropy()
        }
        assertFailsWith<InvalidWordException> {
            Mnemonic(mnemonicPhrase).toSeed()
        }
        // toSeed(validate=false) succeeds!!
        assertEquals(64, Mnemonic(mnemonicPhrase).toSeed(validate = false).size)
    }

    @Test
    fun testInvalidMnemonicWithInvalidNumberOfWords() {
        val mnemonicPhrase = "$validPhrase still"
        assertFailsWith<WordCountException> {
            Mnemonic(mnemonicPhrase).validate()
        }
        assertFailsWith<WordCountException> {
            Mnemonic(mnemonicPhrase).toEntropy()
        }
        assertFailsWith<WordCountException> {
            Mnemonic(mnemonicPhrase).toSeed()
        }
        // toSeed(validate=false) succeeds!!
        assertEquals(64, Mnemonic(mnemonicPhrase).toSeed(validate = false).size)
    }
}
