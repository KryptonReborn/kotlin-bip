package dev.kryptonreborn.bip.bip39

import dev.kryptonreborn.bip.bip39.MnemonicException.*
import dev.kryptonreborn.bip.bip39.WordList.Companion.DEFAULT_LANGUAGE_CODE
import dev.kryptonreborn.bip.bip39.WordList.Companion.computeSentence
import dev.kryptonreborn.bip.bip39.WordList.Companion.getCachedWords
import org.kotlincrypto.hash.sha2.SHA256
import kotlin.experimental.or

/**
 * Encompasses all mnemonic functionality, which helps keep everything concise and in one place.
 */
@OptIn(ExperimentalStdlibApi::class)
class Mnemonic(
    val chars: CharArray,
    private val languageCode: String = DEFAULT_LANGUAGE_CODE,
) : AutoCloseable, Iterable<String> {
    constructor(
        phrase: String,
        languageCode: String = DEFAULT_LANGUAGE_CODE,
    ) : this(phrase.toCharArray(), languageCode)

    constructor(
        entropy: ByteArray,
        languageCode: String = DEFAULT_LANGUAGE_CODE,
    ) : this(computeSentence(entropy), languageCode)

    constructor(
        wordCount: WordCount,
        languageCode: String = DEFAULT_LANGUAGE_CODE,
    ) : this(computeSentence(wordCount.toEntropy()), languageCode)

    val wordCount get() = chars.count { it == ' ' }.let { if (it == 0) it else it + 1 }

    /**
     * Converts the [chars] array into a list of CharArrays for each word. This library does not
     * use this value, but it exposes it as a convenience. In most cases, just working with the
     * chars can be enough.
     */
    val words: List<CharArray>
        get() = ArrayList<CharArray>(wordCount).apply {
            var cursor = 0
            chars.forEachIndexed { i, c ->
                if (c == ' ' || i == chars.lastIndex) {
                    add(chars.copyOfRange(cursor, if (chars[i].isWhitespace()) i else i + 1))
                    cursor = i + 1
                }
            }
        }

    companion object {
        private const val DEFAULT_PASSPHRASE = "mnemonic"
        private const val ITERATION_COUNT = 2048
        private const val KEY_SIZE = 512
    }

    override fun close() = clear()

    override fun iterator(): Iterator<String> = object : Iterator<String> {
        var cursor: Int = 0

        override fun hasNext() = cursor < chars.size - 1

        override fun next(): String {
            val nextSpaceIndex = nextSpaceIndex()
            val word = chars.concatToString(cursor, cursor + (nextSpaceIndex - cursor))
            cursor = nextSpaceIndex + 1
            return word
        }

        private fun nextSpaceIndex(): Int {
            var i = cursor
            while (i < chars.size - 1) {
                if (chars[i].isWhitespace()) return i else i++
            }
            return chars.size
        }
    }

    fun clear() = chars.fill(0.toChar())

    fun isEmpty() = chars.isEmpty()

    fun validate() {
        // verify: word count is supported
        wordCount.let { wordCount ->
            if (WordCount.entries.toTypedArray().none { it.count == wordCount }) {
                throw WordCountException(wordCount)
            }
        }

        // verify: all words are on the list
        var sublist = getCachedWords(languageCode)
        var nextLetter = 0
        chars.forEachIndexed { i, c ->
            // filter down, by character, ensuring that there are always matching words.
            // per BIP39, we could stop checking each word after 4 chars, but we check them all,
            // for completeness
            if (c == ' ') {
                sublist = getCachedWords(languageCode)
                nextLetter = 0
            } else {
                sublist = sublist.filter { it.length > nextLetter && it[nextLetter] == c }
                if (sublist.isEmpty()) throw InvalidWordException(i)
                nextLetter++
            }
        }

        // verify: checksum
        validateChecksum()
    }

    /**
     * Get the original entropy that was used to create this MnemonicCode. This call will fail
     * if the words have an invalid length or checksum.
     *
     * @InvalidWordException If any word isn't in the word list
     * @throws WordCountException when the word count is zero or not a multiple of 3.
     * @throws ChecksumException if the checksum does not match the expected value.
     */
    @Suppress("ThrowsCount", "NestedBlockDepth")
    fun toEntropy(): ByteArray {
        wordCount.let { if (it <= 0 || it % 3 > 0) throw WordCountException(wordCount) }

        // Look up all the words in the list and construct the
        // concatenation of the original entropy and the checksum.
        //
        val totalLengthBits = wordCount * 11
        val checksumLengthBits = totalLengthBits / 33
        val entropy = ByteArray((totalLengthBits - checksumLengthBits) / 8)
        val checksumBits = mutableListOf<Boolean>()

        val words = getCachedWords(languageCode)
        var bitsProcessed = 0
        var nextByte = 0.toByte()
        this.forEach {
            words.binarySearch(it).let { phraseIndex ->
                // fail if the word was not found on the list
                if (phraseIndex < 0) throw InvalidWordException(it)
                // for each of the 11 bits of the phraseIndex
                (10 downTo 0).forEach { i ->
                    // isolate the next bit (starting from the big end)
                    val bit = phraseIndex and (1 shl i) != 0
                    // if the bit is set, then update the corresponding bit in the nextByte
                    if (bit) nextByte = nextByte or (1 shl 7 - (bitsProcessed).rem(8)).toByte()
                    val entropyIndex = ((++bitsProcessed) - 1) / 8
                    // if we're at a byte boundary (excluding the extra checksum bits)
                    if (bitsProcessed.rem(8) == 0 && entropyIndex < entropy.size) {
                        // then set the byte and prepare to process the next byte
                        entropy[entropyIndex] = nextByte
                        nextByte = 0.toByte()
                        // if we're now processing checksum bits, then track them for later
                    } else if (entropyIndex >= entropy.size) {
                        checksumBits.add(bit)
                    }
                }
            }
        }

        // Check each required checksum bit, against the first byte of the sha256 of entropy
        SHA256().digest(entropy)[0].toBits().let { hashFirstByteBits ->
            repeat(checksumLengthBits) { i ->
                // failure means that each word was valid BUT they were in the wrong order
                if (hashFirstByteBits[i] != checksumBits[i]) throw ChecksumException()
            }
        }

        return entropy
    }

    /**
     * Given a mnemonic, create a seed per BIP-0039.
     *
     * Per the proposal, "A user may decide to protect their mnemonic with a passphrase. If a
     * passphrase is not present, an empty string "" is used instead. To create a binary seed from
     * the mnemonic, we use the PBKDF2 function with a mnemonic sentence (in UTF-8 NFKD) used as the
     * password and the string "mnemonic" + passphrase (again in UTF-8 NFKD) used as the salt. The
     * iteration count is set to 2048 and HMAC-SHA512 is used as the pseudo-random function. The
     * length of the derived key is 512 bits (= 64 bytes).
     *
     * @param passphrase an optional password to protect the phrase. Defaults to an empty string. This
     * gets added to the salt. Note: it is expected that the passphrase has been normalized via a call
     * to something like `Normalizer.normalize(passphrase, Normalizer.Form.NFKD)` but this only becomes
     * important when additional language support is added.
     * @param validate true to validate the mnemonic before attempting to generate the seed. This
     * can add a bit of extra time to the calculation and is mainly only necessary when the seed is
     * provided by user input. Meaning, in most cases, this can be false, but we default to `true` to
     * be "safe by default."
     */
    fun toSeed(
        // expect: UTF-8 normalized with NFKD
        passphrase: CharArray = charArrayOf(),
        validate: Boolean = true,
    ): ByteArray {
        // we can skip validation when we know for sure that the code is valid
        // such as when it was just generated from new/correct entropy (common case for new seeds)
        if (validate) validate()
        return (DEFAULT_PASSPHRASE.toCharArray() + passphrase).toBytes().let { salt ->
            PBKDF2.pbkdf2WithHmacSHA512(chars.toBytes(), salt, ITERATION_COUNT, KEY_SIZE)
        }
    }

    /**
     * Convenience method for validating the checksum of this MnemonicCode. Since validation
     * requires deriving the original entropy, this function is the same as calling [toEntropy].
     */
    private fun validateChecksum() = toEntropy()
}

internal fun ByteArray.toBits(): List<Boolean> = flatMap { it.toBits() }

internal fun Byte.toBits(): List<Boolean> =
    (7 downTo 0).map { (toInt() and (1 shl it)) != 0 }

internal fun CharArray.toBytes(): ByteArray {
    return map { it.code.toByte() }.toByteArray()
}
