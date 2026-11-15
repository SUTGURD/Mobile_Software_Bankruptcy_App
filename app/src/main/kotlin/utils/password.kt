package utils

import java.security.MessageDigest
import java.nio.charset.StandardCharsets

fun hashPassword(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
        .digest(password.toByteArray(StandardCharsets.UTF_8))
    return digest.joinToString(separator = "") { byte -> "%02x".format(byte.toInt() and 0xff) }
}

fun verifyPassword(password: String, expectedHash: String): Boolean {
    if (expectedHash.length != 64) return false

    val expectedDigest = ByteArray(32)
    for (index in expectedDigest.indices) {
        val high = Character.digit(expectedHash[index * 2], 16)
        val low = Character.digit(expectedHash[index * 2 + 1], 16)
        if (high == -1 || low == -1) return false
        expectedDigest[index] = ((high shl 4) or low).toByte()
    }

    val actualDigest = MessageDigest.getInstance("SHA-256")
        .digest(password.toByteArray(StandardCharsets.UTF_8))
    return MessageDigest.isEqual(actualDigest, expectedDigest)
}
