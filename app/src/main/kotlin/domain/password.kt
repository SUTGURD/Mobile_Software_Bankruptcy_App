package domain

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val PBKDF2_PREFIX = "pbkdf2-sha256"
private const val HASH_SEPARATOR = '$'
private const val PBKDF2_ITERATIONS = 100_000
private const val MAX_ACCEPTED_ITERATIONS = 10_000_000
private const val SALT_BYTES = 16
private const val KEY_BYTES = 32
private const val LEGACY_HASH_LENGTH = 64

/**
 * Returns a salted hash in the format `pbkdf2-sha256$<iterations>$<salt hex>$<key hex>`.
 * The iteration count is stored in the hash, so it can be raised later without
 * invalidating existing hashes.
 */
fun hashPassword(password: String): String {
    require(password.isNotEmpty()) { "The password must not be empty." }
    val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
    val key = pbkdf2Sha256(password, salt, PBKDF2_ITERATIONS)
    return listOf(PBKDF2_PREFIX, PBKDF2_ITERATIONS.toString(), salt.toHex(), key.toHex())
        .joinToString(HASH_SEPARATOR.toString())
}

/**
 * Accepts both the salted format and the unsalted SHA-256 hex digest that earlier
 * versions stored, so existing accounts can still log in.
 */
fun verifyPassword(password: String, expectedHash: String): Boolean {
    if (password.isEmpty()) return false
    return if (expectedHash.startsWith(PBKDF2_PREFIX + HASH_SEPARATOR)) {
        verifyPbkdf2Hash(password, expectedHash)
    } else {
        verifyLegacySha256Hash(password, expectedHash)
    }
}

internal fun legacySha256Hash(password: String): String =
    MessageDigest.getInstance("SHA-256")
        .digest(password.toByteArray(StandardCharsets.UTF_8))
        .toHex()

internal fun pbkdf2Sha256(password: String, salt: ByteArray, iterations: Int): ByteArray {
    require(password.isNotEmpty() && iterations > 0)
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(password.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))
    var block = mac.doFinal(salt + byteArrayOf(0, 0, 0, 1))
    val key = block.copyOf()
    repeat(iterations - 1) {
        block = mac.doFinal(block)
        for (index in key.indices) {
            key[index] = (key[index].toInt() xor block[index].toInt()).toByte()
        }
    }
    return key
}

private fun verifyPbkdf2Hash(password: String, expectedHash: String): Boolean {
    val parts = expectedHash.split(HASH_SEPARATOR)
    if (parts.size != 4) return false
    val iterations = parts[1].toIntOrNull()
        ?.takeIf { it in 1..MAX_ACCEPTED_ITERATIONS }
        ?: return false
    val salt = parts[2].fromHex()?.takeIf { it.isNotEmpty() } ?: return false
    val expectedKey = parts[3].fromHex()?.takeIf { it.size == KEY_BYTES } ?: return false
    return MessageDigest.isEqual(pbkdf2Sha256(password, salt, iterations), expectedKey)
}

private fun verifyLegacySha256Hash(password: String, expectedHash: String): Boolean {
    if (expectedHash.length != LEGACY_HASH_LENGTH) return false
    val expectedDigest = expectedHash.fromHex() ?: return false
    val actualDigest = MessageDigest.getInstance("SHA-256")
        .digest(password.toByteArray(StandardCharsets.UTF_8))
    return MessageDigest.isEqual(actualDigest, expectedDigest)
}

private fun ByteArray.toHex(): String =
    joinToString(separator = "") { byte -> "%02x".format(byte.toInt() and 0xff) }

private fun String.fromHex(): ByteArray? {
    if (length % 2 != 0) return null
    val bytes = ByteArray(length / 2)
    for (index in bytes.indices) {
        val high = Character.digit(this[index * 2], 16)
        val low = Character.digit(this[index * 2 + 1], 16)
        if (high == -1 || low == -1) return null
        bytes[index] = ((high shl 4) or low).toByte()
    }
    return bytes
}
