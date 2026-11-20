package domain

import data.DummyData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordTest {
    @Test
    fun pbkdf2MatchesPublishedHmacSha256Vectors() {
        val salt = "salt".toByteArray()

        assertEquals(
            "120fb6cffcf8b32c43e7225256c4f837a86548c92ccc35480805987cb70be17b",
            pbkdf2Sha256("password", salt, 1).toHexString()
        )
        assertEquals(
            "ae4d0c95af6b46d32d0adff928f06dd02a303f8ef3c251dfd6e2d85a95474c43",
            pbkdf2Sha256("password", salt, 2).toHexString()
        )
        assertEquals(
            "c5e478d59288c841aa530db6845c4c8d962893a001ce4e11a4963873aa98134a",
            pbkdf2Sha256("password", salt, 4096).toHexString()
        )
    }

    @Test
    fun hashPasswordUsesVersionedSaltedFormat() {
        val first = hashPassword("password")
        val second = hashPassword("password")

        assertTrue(first.startsWith("pbkdf2-sha256\$100000\$"))
        assertEquals(4, first.split('$').size)
        assertNotEquals(first, second)
    }

    @Test
    fun verifyPasswordAcceptsMatchingPasswordAndRejectsOtherPasswords() {
        val hash = hashPassword("correct horse")

        assertTrue(verifyPassword("correct horse", hash))
        assertFalse(verifyPassword("incorrect horse", hash))
        assertFalse(verifyPassword("", hash))
    }

    @Test
    fun verifyPasswordStillAcceptsLegacyUnsaltedHashes() {
        val legacyHash = legacySha256Hash("password")

        assertEquals(
            "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8",
            legacyHash
        )
        assertTrue(verifyPassword("password", legacyHash))
        assertFalse(verifyPassword("other", legacyHash))
    }

    @Test
    fun verifyPasswordRejectsMalformedHashes() {
        assertFalse(verifyPassword("password", "not-a-hash"))
        assertFalse(verifyPassword("password", "g".repeat(64)))
        assertFalse(verifyPassword("password", "pbkdf2-sha256\$0\$00\$00"))
        assertFalse(verifyPassword("password", "pbkdf2-sha256\$100000\$zz\$zz"))
        assertFalse(verifyPassword("password", "pbkdf2-sha256\$100000\$00\$00"))
    }

    @Test
    fun dummyUsersHaveHashedPasswordsInListOrder() {
        DummyData.users.forEachIndexed { index, user ->
            assertTrue(verifyPassword("123${index + 1}", user.passwordHash))
        }
    }

    private fun ByteArray.toHexString(): String =
        joinToString(separator = "") { byte -> "%02x".format(byte.toInt() and 0xff) }
}
