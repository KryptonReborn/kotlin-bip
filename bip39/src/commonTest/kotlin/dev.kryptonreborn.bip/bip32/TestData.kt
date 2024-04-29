package dev.kryptonreborn.bip.bip32

import com.goncalossilva.resources.Resource
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class EnglishTestDataJson(
    val entropy: String,
    val mnemonic: String,
    val seed: String,
)

fun loadEnglishTestDataJson(): List<EnglishTestDataJson> {
    val json = Json {
        ignoreUnknownKeys = true
    }

    // Uses test values from the original BIP : https://github.com/trezor/python-mnemonic/blob/master/vectors.json
    return json.decodeFromString(Resource("src/commonTest/resources/bip39/english_test_data.json").readText())
}

@Serializable
class Pbkdf2TestDataJson(
    val password: String,
    val salt: String,
    val count: Int,
    val length: Int,
    val expected: String,
)

fun loadPbkdf2TestDataJson(): List<Pbkdf2TestDataJson> {
    val json = Json {
        ignoreUnknownKeys = true
    }

    // modified from https://stackoverflow.com/a/19898265/178433
    return json.decodeFromString(Resource("src/commonTest/resources/bip39/pbkdf2_test_data.json").readText())
}
