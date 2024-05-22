package dev.kryptonreborn.bip.bip44

object PathElements {
    /**
     * Given a BIP-32 style path like "m/44'/1'/0'/420'", generate a list of [PathElement] represented the parsed path.
     *
     * @param path The BIP-32 style derivation path to parse.
     * @return The parsed path as a list of [PathElement] instances.
     */
    fun from(path: String): List<PathElement> = path.parseBIP44Path()
}

internal fun String.parseBIP44Path(): List<PathElement> {
    val s = split("/")
    require(s[0] == "m") { "No root account m/" }
    require(s.size <= 6) { "bip44 path too deep" }
    return s.drop(1).mapIndexed { position, part ->
        val l = part.takeWhile { c -> c.isDigit() }
        val n = l.toInt()
        val r = part.substring(l.length, part.length)
        val hardened = r == "\'" || r.lowercase() == "h"
        require(r.isEmpty() || hardened) { "Invalid hardening: $r" }
        buildPathElement(position, n, hardened)
    }
}

internal fun List<PathElement>.toPathString() = (listOf("m") + map { it.toString() }).joinToString("/")

internal fun buildPathElement(
    position: Int,
    number: Int,
    hardened: Boolean,
): PathElement =
    when (position) {
        0 -> PathElement.Purpose(number, hardened)
        1 -> PathElement.CoinType(number, hardened)
        2 -> PathElement.Account(number, hardened)
        3 -> PathElement.Change(number, hardened)
        4 -> PathElement.Index(number, hardened)
        else -> error("Invalid path position: $position")
    }

/**
 * Represents the individual elements of a BIP44-style derivation path, providing
 * typed representations of the components of the derivation path.
 *
 * See https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki
 */
sealed class PathElement(open val number: Int, open val hardened: Boolean) {
    private fun <R> Boolean.into(
        t: R,
        f: R,
    ): R = if (this) t else f

    override fun toString(): String = "$number${hardened.into("'", "")}"

    data class Purpose(override val number: Int, override val hardened: Boolean) : PathElement(number, hardened) {
        override fun toString(): String = super.toString()
    }

    data class CoinType(override val number: Int, override val hardened: Boolean) : PathElement(number, hardened) {
        override fun toString(): String = super.toString()
    }

    data class Account(override val number: Int, override val hardened: Boolean) : PathElement(number, hardened) {
        override fun toString(): String = super.toString()
    }

    data class Change(override val number: Int, override val hardened: Boolean) : PathElement(number, hardened) {
        override fun toString(): String = super.toString()
    }

    data class Index(override val number: Int, override val hardened: Boolean) : PathElement(number, hardened) {
        override fun toString(): String = super.toString()
    }
}
