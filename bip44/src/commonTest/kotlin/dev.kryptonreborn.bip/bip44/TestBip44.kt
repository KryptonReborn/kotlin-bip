package dev.kryptonreborn.bip.bip44

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TestBip44 {
    private data class TestVector(
        val path: String,
        val parsed: List<PathElement> = emptyList(),
        val valid: Boolean = true
    )

    private val testVector = { vector: TestVector ->
        val parsed = runCatching { vector.path.parseBIP44Path() }
        assertEquals(vector.valid, parsed.isSuccess, "${vector.path} valid")
        if (vector.valid) {
            val res = parsed.getOrThrow()
            assertEquals(vector.parsed.size, res.size, "${vector.path} size")
            assertEquals(vector.parsed, res, "${vector.path} contents")
        }
    }

    private fun pathOf(vararg pairs: Pair<Int, Boolean>): List<PathElement> =
        pairs.mapIndexed { position, (number, hardened) -> buildPathElement(position, number, hardened) }

    @Test
    fun checkBasicPathParsing() {
        val vectors = listOf(
            TestVector("", valid = false),
            TestVector("u", valid = false),
            TestVector("m"),
            TestVector("m/", valid = false),
            TestVector("m/0", pathOf(0 to false)),
            TestVector("m/1H/1H/1H", pathOf(1 to true, 1 to true, 1 to true)),
            TestVector("m/1h/1h/1h", pathOf(1 to true, 1 to true, 1 to true)),
            TestVector("m/1'/1'/1'", pathOf(1 to true, 1 to true, 1 to true)),
            TestVector("m/1/1/1/1/1", pathOf(1 to false, 1 to false, 1 to false, 1 to false, 1 to false)),
            TestVector("m/1/1/1/1/1/1", valid = false),
            TestVector("m/1m/1m/1m", valid = false),
        )
        vectors.forEach(testVector)
    }

    @Test
    fun parseTooLongPath() {
        assertFailsWith<IllegalArgumentException> {
            "m/1/1/1/1/1/1".parseBIP44Path()
        }
        assertFailsWith<IllegalArgumentException> {
            DerivationPath.from("m/1/1/1/1/1/1")
        }
    }

    @Test
    fun parseTooShortPath() {
        assertFailsWith<IllegalArgumentException> {
            DerivationPath.from("m/44'/505'/0'")
        }
        assertFailsWith<IllegalArgumentException> {
            DerivationPath.from("m/44'/505'/0'/1")
        }
    }

    @Test
    fun parsePathToDerivationPath() {
        val path = DerivationPath.from("m/44'/505'/0'/0/0'")
        assertEquals("m/44'/505'/0'/0/0'", path.toString())
        assertEquals(path.purpose, PathElement.Purpose(44, hardened = true))
        assertEquals(path.coinType, PathElement.CoinType(505, hardened = true))
        assertEquals(path.account, PathElement.Account(0, hardened = true))
        assertEquals(path.change, PathElement.Change(0, hardened = false))
        assertEquals(path.index, PathElement.Index(0, hardened = true))
    }

    @Test
    fun checkPathBuilder() {
        val path = DerivationPath.from("m/44'/505'/0'/0/0'")
        val newPath = path.toBuilder().build()
        assertEquals(path, newPath)
    }

    @Test
    fun createNewPathWithUpdatedAccount() {
        val path = DerivationPath.from("m/44'/505'/0'/0/0'")
        val newAccount = 789
        val newPath = path.toBuilder().account(newAccount, harden = false).build()
        assertEquals(newPath.account, PathElement.Account(newAccount, hardened = false))
        assertEquals("m/44'/505'/$newAccount/0/0'", newPath.toString())
    }

    @Test
    fun createNewPathWithUpdatedIndex() {
        val path = DerivationPath.from("m/44'/505'/0'/0/0'")
        val newIndex = 789
        val newPath = path.toBuilder().index(newIndex, harden = false).build()
        assertEquals(newPath.index, PathElement.Index(newIndex, hardened = false))
        assertEquals("m/44'/505'/0'/0/$newIndex", newPath.toString())
    }

    @Test
    fun createNewPathWithUpdatedAccountAndIndex() {
        val path = DerivationPath.from("m/44'/505'/0'/0/0'")
        val newAccount = 987
        val newIndex = 789
        val newPath = path.toBuilder()
            .account(newAccount, harden = false)
            .index(newIndex, harden = true)
            .build()
        assertEquals(newPath.account, PathElement.Account(newAccount, hardened = false))
        assertEquals(newPath.index, PathElement.Index(newIndex, hardened = true))
        assertEquals("m/44'/505'/$newAccount/0/${newIndex}'", newPath.toString())
    }
}
