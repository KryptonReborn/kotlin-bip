package dev.kryptonreborn.bip.crypto

import com.goncalossilva.resources.Resource
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class Pbkdf2TestDataJson(
    val password: String,
    val salt: String,
    val count: Int,
    val length: Int,
    val expected: String,
)

fun loadPbkdf2TestDataJson(): List<Pbkdf2TestDataJson> {
    val json =
        Json {
            ignoreUnknownKeys = true
        }

    // modified from https://stackoverflow.com/a/19898265/178433
    return json.decodeFromString(Resource("src/commonTest/resources/crypto/pbkdf2_test_data.json").readText())
}
