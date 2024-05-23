package dev.kryptonreborn.bip.bip39

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
    val json =
        Json {
            ignoreUnknownKeys = true
        }

    // Uses test values from the original BIP : https://github.com/trezor/python-mnemonic/blob/master/vectors.json
    return json.decodeFromString(Resource("src/commonTest/resources/bip39/english_test_data.json").readText())
}
