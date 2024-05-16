package dev.kryptonreborn.bip.bip32

import com.ionspin.kotlin.bignum.integer.BigInteger
import dev.kryptonreborn.bip.bip32.key.Curve
import dev.kryptonreborn.bip.bip32.key.HDPrivateKey
import dev.kryptonreborn.bip.bip32.key.HDPublicKey
import dev.kryptonreborn.bip.bip32.util.BytesUtil
import dev.kryptonreborn.bip.bip32.util.BytesUtil.parse256
import dev.kryptonreborn.bip.bip32.util.Secp256k1Util.point
import dev.kryptonreborn.bip.bip32.util.HashUtil.h160
import dev.kryptonreborn.bip.crypto.PBKDF2
import dev.kryptonreborn.ecdsa.EcPoint
import dev.kryptonreborn.ecdsa.Secp256k1
import dev.kryptonreborn.ed25519.newKeyPairFromSeed

object HDKeyGenerator {
    const val MASTER_PATH: String = "m"

    fun getAddressFromSeed(seed: ByteArray, network: Network, coinType: CoinType): HDAddress {
        val curve: Curve = coinType.curve
        val I: ByteArray = PBKDF2.hmacSHA512(
            curve.seed.encodeToByteArray(), seed
        )

        //split into left/right
        val IL: ByteArray = I.copyOfRange(0, 32)
        val IR: ByteArray = I.copyOfRange(32, 64)

        val masterSecretKey: BigInteger = parse256(IL)

        //In case IL is 0 or >=n, the master key is invalid.
        if (curve !== Curve.ed25519 && masterSecretKey.compareTo(BigInteger.ZERO) == 0 || masterSecretKey > Secp256k1.n
        ) {
            throw HDKeyException("The master key is invalid")
        }

        val point: EcPoint = point(masterSecretKey)

        val privateKey = HDPrivateKey(
            version = network.privateKeyVersion,
            depth = 0,
            fingerprint = byteArrayOf(0, 0, 0, 0),
            childNumber = byteArrayOf(0, 0, 0, 0),
            chainCode = IR,
            keyData = byteArrayOf(0).plus(IL)
        )
        val publicKey = HDPublicKey(
            version = network.publicKeyVersion,
            depth = 0,
            fingerprint = byteArrayOf(0, 0, 0, 0),
            childNumber = byteArrayOf(0, 0, 0, 0),
            chainCode = IR,
            keyData = serPoint(point)
        )

        when (curve) {
            Curve.bitcoin -> {
                privateKey.privateKey = privateKey.keyData
                publicKey.publicKey = publicKey.keyData
            }

            Curve.ed25519 -> {
                privateKey.privateKey = IL
                val keyPair = newKeyPairFromSeed(IL)
                publicKey.publicKey = byteArrayOf(0).plus(keyPair.publicKey)
            }
        }
        return HDAddress(privateKey, publicKey, coinType, MASTER_PATH)
    }

    fun getAddress(parent: HDAddress, child: Long, isHardened: Boolean): HDAddress {
        var _child = child

        if (isHardened) {
            _child += 0x80000000L
        } else if (parent.coinType.curve === Curve.ed25519) {
            throw HDKeyException("ed25519 only supports hardened keys")
        }

        val xChain: ByteArray = parent.privateKey.chainCode
        ///backwards hmac order in method?
        val I: ByteArray
        if (isHardened) {
            //If so (hardened child): let I = HMAC-SHA512(Key = cpar, Data = 0x00 || ser256(kpar) || ser32(i)). (Note: The 0x00 pads the private key to make it 33 bytes long.)
            val kpar: BigInteger = parse256(parent.privateKey.keyData)
            var data: ByteArray = byteArrayOf(0).plus(BytesUtil.ser256(kpar))
            data = data.plus(BytesUtil.ser32(_child))
            I = PBKDF2.hmacSHA512(xChain, data)
        } else {
            //I = HMAC-SHA512(Key = cpar, Data = serPoint(point(kpar)) || ser32(i))
            //just use public key
            val key: ByteArray = parent.publicKey.keyData
            val xPubKey: ByteArray = key.plus(BytesUtil.ser32(_child))
            I = PBKDF2.hmacSHA512(xChain, xPubKey)
        }

        //split into left/right
        val IL: ByteArray = I.copyOfRange(0, 32)
        val IR: ByteArray = I.copyOfRange(32, 64)

        //The returned child key ki is parse256(IL) + kpar (mod n).
        val parse256: BigInteger = parse256(IL)
        val kpar: BigInteger = parse256(parent.privateKey.keyData)
        val childSecretKey: BigInteger = parse256.add(kpar).mod(Secp256k1.n)

        val point: EcPoint = point(childSecretKey)

        // can just use fingerprint, but let's use data from parent public key
        val pKd: ByteArray = parent.publicKey.keyData
        var h160: ByteArray = h160(pKd)

        val childNumber = BytesUtil.ser32(_child)

        val privateKey = HDPrivateKey(
            version = parent.privateKey.version,
            depth = parent.privateKey.depth + 1,
            fingerprint = getFingerprint(parent.privateKey.keyData),
            childNumber = childNumber,
            chainCode = IR,
            keyData = byteArrayOf(0).plus(BytesUtil.ser256(childSecretKey))
        )
        val publicKey = HDPublicKey(
            version = parent.publicKey.version,
            depth = parent.publicKey.depth + 1,
            fingerprint = byteArrayOf(h160[0], h160[1], h160[2], h160[3]),
            childNumber = childNumber,
            chainCode = IR,
            keyData = serPoint(point)
        )

        when (parent.coinType.curve) {
            Curve.bitcoin -> {
                privateKey.privateKey = privateKey.keyData
                publicKey.publicKey = publicKey.keyData
            }

            Curve.ed25519 -> {
                privateKey.privateKey = IL
                h160 = h160(parent.publicKey.publicKey)
                val childFingerprint = byteArrayOf(h160[0], h160[1], h160[2], h160[3])
                publicKey.fingerprint = childFingerprint
                privateKey.fingerprint = childFingerprint
                privateKey.keyData = byteArrayOf(0).plus(IL)
                val keyPair = newKeyPairFromSeed(IL)
                publicKey.publicKey = byteArrayOf(0).plus(keyPair.publicKey)
            }
        }

        return HDAddress(
            privateKey, publicKey, parent.coinType,
            getPath(parent.path, _child, isHardened)
        )
    }

    private fun getPath(parentPath: String, child: Long, isHardened: Boolean): String {
        var _parentPath: String? = parentPath
        if (_parentPath == null) {
            _parentPath = MASTER_PATH
        }
        return _parentPath + "/" + child + (if (isHardened) "'" else "")
    }

    /**
     * Get the fingerprint
     *
     * @param keyData key data
     * @return fingerprint
     */
    private fun getFingerprint(keyData: ByteArray): ByteArray {
        val point: ByteArray = serPoint(point(parse256(keyData)))
        val h160: ByteArray = h160(point)
        return byteArrayOf(h160[0], h160[1], h160[2], h160[3])
    }

    /**
     * serPoint(P): serializes the coordinate pair P = (x,y) as a byte sequence using
     * SEC1's compressed form: (0x02 or 0x03) || ser256(x), where the header byte
     * depends on the parity of the omitted y coordinate.
     *
     * @param point EcPoint
     * @return serialized point
     */
    private fun serPoint(point: EcPoint): ByteArray {
        val pad = if (point.y.mod(BigInteger(2)) == BigInteger.ZERO) byteArrayOf(2) else byteArrayOf(3)
        if (point.xByteArray.first() == 0.toByte()) {
            return pad.plus(point.xByteArray.copyOfRange(1, point.xByteArray.size))
        }
        return pad.plus(point.xByteArray)
    }
}
