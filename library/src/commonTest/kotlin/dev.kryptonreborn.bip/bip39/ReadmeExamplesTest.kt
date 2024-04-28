package dev.kryptonreborn.bip.bip39

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@OptIn(ExperimentalStdlibApi::class)
class ReadmeExamplesTest {
    private val validPhrase = "still champion voice habit trend flight survey between bitter process artefact blind" +
            " carbon truly provide dizzy crush flush breeze blouse charge solid fish spread"

    @Test
    fun testCreateMnemonicPhrase() {
        val mnemonic = Mnemonic(WordCount.COUNT_24)
        assertEquals(24, mnemonic.wordCount)
        assertEquals(Unit, mnemonic.validate())
    }

    @Test
    fun testGenerateSeed() {
        val mnemonic = Mnemonic(WordCount.COUNT_24)
        val seed = mnemonic.toSeed()
        assertEquals(24, mnemonic.wordCount)
        assertEquals(Unit, mnemonic.validate())
        assertEquals(64, seed.size)
    }

    @Test
    fun testGenerateSeedFromExistingMnemonicChars() {
        val mnemonic = Mnemonic(validPhrase.toCharArray())
        val seed = mnemonic.toSeed()
        assertEquals(24, mnemonic.wordCount)
        assertEquals(Unit, mnemonic.validate())
        assertEquals(64, seed.size)
    }

    @Test
    fun testGenerateSeedFromExistingMnemonicString() {
        val mnemonic = Mnemonic(validPhrase)
        val seed = mnemonic.toSeed()
        assertEquals(24, mnemonic.wordCount)
        assertEquals(Unit, mnemonic.validate())
        assertEquals(64, seed.size)
    }

    @Test
    fun testGenerateSeedWithPassphraseNormalWay() {
        val passphrase = "bitcoin".toCharArray()
        val seed = Mnemonic(validPhrase).toSeed(passphrase)
        assertEquals(64, seed.size)
    }

    @Test
    fun testGenerateSeedWithPassphrasePrivateWay() {
        var seed: ByteArray
        charArrayOf('z', 'c', 'a', 's', 'h').let { passphrase ->
            seed = Mnemonic(validPhrase).toSeed(passphrase)
            assertEquals("zcash", passphrase.concatToString())
            passphrase.fill('0')
            assertEquals("00000", passphrase.concatToString())
        }
        assertEquals(64, seed.size)
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
}
