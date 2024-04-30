package dev.kryptonreborn.bip.bip32

import dev.kryptonreborn.bip.bip39.MnemonicException.*
import dev.kryptonreborn.bip.bip39.Mnemonic
import dev.kryptonreborn.bip.bip39.WordCount
import dev.kryptonreborn.bip.bip39.WordList.Companion.DEFAULT_LANGUAGE_CODE
import kotlin.test.*

@OptIn(ExperimentalStdlibApi::class)
class MnemonicTest {
    private val validPhrase = "void come effort suffer camp survey warrior heavy shoot primary" +
            " clutch crush open amazing screen patrol group space point ten exist slush involve unfold"

    private val englishTestData = loadEnglishTestDataJson()

    @Test
    fun testConvertValidMnemonicToSeed() {
        val seed = Mnemonic(validPhrase).toSeed()
        val hex = seed.toHexString()
        assertEquals(64, seed.size)
        assertEquals(128, hex.length)
        assertEquals(
            "b873212f885ccffbf4692afcb84bc2e55886de2dfa07d90f5c3c239abc31c0a6ce047e30fd8bf6a281e71389aa82d73df74c7bbfb3b06b4639a5cee775cccd3c",
            hex
        )
    }

    @Test
    fun testConvertValidMnemonicCharArrayToSeed() {
        val mnemonic = Mnemonic(validPhrase.toCharArray())
        val seed = mnemonic.toSeed()
        assertEquals(24, mnemonic.wordCount)
        assertEquals(Unit, mnemonic.validate())
        assertEquals(64, seed.size)
    }

    @Test
    fun testGenerateSeedWithPassphrase() {
        val passphrase = "bitcoin".toCharArray()
        val seed = Mnemonic(validPhrase).toSeed(passphrase)
        assertEquals(64, seed.size)
    }

    @Test
    fun testWordCountBitLength() {
        val listTest =
            listOf(Pair(12, 128), Pair(15, 160), Pair(18, 192), Pair(21, 224), Pair(24, 256))
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
                assertEquals(Unit, mnemonic.validate())
                assertEquals(wordCount.count - 1, mnemonic.chars.count { it == ' ' })
                assertEquals(wordCount.count, words.size)
                assertContentEquals(words, mnemonicPhrase.split(' '))
            }
        }
    }

    @Test
    fun testConvertEntropyToMnemonic() {
        englishTestData.forEach {
            assertEquals(it.mnemonic, Mnemonic(it.entropy.hexToByteArray()).chars.concatToString())
        }
    }

    @Test
    fun testConvertMnemonicToEntropy() {
        englishTestData.forEach {
            assertEquals(it.entropy, Mnemonic(it.mnemonic).toEntropy().toHexString())
        }
    }

    @Test
    fun testConvertMnemonicToSeed() {
        val passphrase = "TREZOR".toCharArray()
        englishTestData.forEach {
            assertEquals(
                it.seed,
                Mnemonic(it.mnemonic, DEFAULT_LANGUAGE_CODE).toSeed(passphrase)
                    .toHexString()
            )
        }
    }

    @Test
    fun testInvalidMnemonicBySwap() {
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

    @Test
    fun testIterateMnemonicWithForLoop() {
        val mnemonic = Mnemonic(validPhrase)
        var count = 0
        for (word in mnemonic) {
            count++
            assertContains(validPhrase, word)
        }
        assertEquals(24, count)
    }

    @Test
    fun testIterateMnemonicWithForEach() {
        val mnemonic = Mnemonic(validPhrase)
        var count = 0
        mnemonic.forEach { word ->
            count++
            assertContains(validPhrase, word)
        }
        assertEquals(24, count)
    }

    @Test
    fun testAutoClear() {
        val mnemonic = Mnemonic(WordCount.COUNT_24)
        mnemonic.use {
            assertEquals(24, mnemonic.wordCount)
        }
        assertEquals(0, mnemonic.wordCount)
    }

    private fun String.swap(
        srcWord: Int,
        destWord: Int = srcWord + 1,
    ): String {
        require(srcWord < destWord) { "srcWord must be less than destWord" }
        require(destWord <= count { it == ' ' }) { "there aren't that many words" }

        return split(' ').let { words ->
            words.reduceIndexed { i, result, word ->
                val next = when (i) {
                    srcWord -> words[destWord]
                    destWord -> words[srcWord]
                    else -> word
                }
                if (srcWord == 0 && i == 1) "${words[destWord]} $next" else "$result $next"
            }
        }
    }
}
