package dev.kryptonreborn.bip.bip39.utils

import com.goncalossilva.resources.Resource
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class TestDataJson(
    val entropy: String,
    val mnemonic: String,
    val seed: String,
)

fun loadTestDataJson(): List<TestDataJson> {
    val json = Json {
        ignoreUnknownKeys = true
    }

    return json.decodeFromString(Resource("src/commonTest/resources/bip39/english_test_data.json").readText())
}
