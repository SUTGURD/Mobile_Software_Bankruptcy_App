package utils

import data.DummyData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordTest {
    @Test
    fun hashPasswordReturnsSha256HexDigest() {
        assertEquals(
            "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8",
            hashPassword("password")
        )
    }

    @Test
    fun verifyPasswordAcceptsMatchingPasswordAndRejectsOtherPasswords() {
        val hash = hashPassword("correct horse")

        assertTrue(verifyPassword("correct horse", hash))
        assertFalse(verifyPassword("incorrect horse", hash))
    }

    @Test
    fun verifyPasswordRejectsMalformedHashes() {
        assertFalse(verifyPassword("password", "not-a-hash"))
        assertFalse(verifyPassword("password", "g".repeat(64)))
    }

    @Test
    fun dummyUsersHaveHashedPasswordsInListOrder() {
        DummyData.users.forEachIndexed { index, user ->
            assertTrue(verifyPassword("123${index + 1}", user.passwordHash))
        }
    }
}
