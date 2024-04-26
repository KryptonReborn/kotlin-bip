package dev.kryptonreborn.bip.bip39

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

@OptIn(ExperimentalStdlibApi::class)
class ReadmeExamplesTest : ShouldSpec({
    val validPhrase =
        "still champion voice habit trend flight survey between bitter process artefact blind carbon truly provide dizzy crush flush breeze blouse charge solid fish spread"
    context("Example: Create 24-word mnemonic phrase") {
        val mnemonic = Mnemonic(WordCount.COUNT_24)
        should("result in a valid 24-word phrase") {
            mnemonic.wordCount shouldBe 24
        }
        should("result in a valid phrase") {
            shouldNotThrowAny {
                mnemonic.validate()
            }
        }
    }
    context("Example: Generate seed") {
        val mnemonic = Mnemonic(WordCount.COUNT_24)
        should("result in a valid 24-word phrase") {
            mnemonic.toSeed()
            mnemonic.wordCount shouldBe 24
        }
        should("result in a valid phrase") {
            shouldNotThrowAny {
                mnemonic.validate()
            }
        }
    }
    context("Example: Generate seed from existing mnemonic chars") {
        val mnemonic = Mnemonic(validPhrase.toCharArray())
        should("result in a valid 24-word phrase") {
            mnemonic.toSeed()
            mnemonic.wordCount shouldBe 24
        }
        should("result in a valid phrase") {
            shouldNotThrowAny {
                mnemonic.validate()
            }
        }
    }
    context("Example: Generate seed from existing mnemonic string") {
        val mnemonic = Mnemonic(validPhrase)
        should("result in a valid 24-word phrase") {
            mnemonic.toSeed()
            mnemonic.wordCount shouldBe 24
        }
        should("result in a valid phrase") {
            shouldNotThrowAny {
                mnemonic.validate()
            }
        }
    }
    context("Example: Generate seed with passphrase") {
        val passphrase = "bitcoin".toCharArray()
        should("'normal way' results in a 64 byte seed") {
            val seed = Mnemonic(validPhrase).toSeed(passphrase)
            seed.size shouldBe 64
        }
        should("'private way' results in a 64 byte seed") {
            var seed: ByteArray
            charArrayOf('z', 'c', 'a', 's', 'h').let { passphrase ->
                seed = Mnemonic(validPhrase).toSeed(passphrase)
                passphrase.concatToString() shouldBe "zcash"
                passphrase.fill('0')
                passphrase.concatToString() shouldBe "00000"
            }
            seed.size shouldBe 64
        }
    }
    context("Example: Iterate over mnemonic codes") {
        val mnemonic = Mnemonic(validPhrase)
        should("work in a for loop") {
            var count = 0
            for (word in mnemonic) {
                count++
                validPhrase shouldContain word
            }
            count shouldBe 24
        }
        should("work with forEach") {
            var count = 0
            mnemonic.forEach { word ->
                count++
                validPhrase shouldContain word
            }
            count shouldBe 24
        }
    }
    context("Example: auto-clear") {
        should("clear the mnemonic when done") {
            val mnemonic = Mnemonic(WordCount.COUNT_24)
            mnemonic.use {
                mnemonic.wordCount shouldBe 24
            }

            // content gets automatically cleared after use!
            mnemonic.wordCount shouldBe 0
        }
    }
})
