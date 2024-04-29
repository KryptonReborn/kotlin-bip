package dev.kryptonreborn.bip.bip39

sealed class MnemonicException(message: String) : Throwable(message) {
    class InvalidWordException : MnemonicException {
        constructor(index: Int) : super("Error: invalid word encountered at index $index.")
        constructor(word: String) : super("Error: <$word> was not found in the word list.")
    }

    class WordCountException(count: Int) : MnemonicException("Error: $count is an invalid word count.")

    class ChecksumException : MnemonicException("Error: The checksum failed. Verify that none of the words have been transposed.")
}
