package com.example.application_0

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DatabaseResetCredentialsTest {
    @Test
    fun acceptsExactResetCredentials() {
        assertTrue(isDatabaseResetCredentials("RESETDATABASE", "RESETDATABASE"))
    }

    @Test
    fun rejectsPartialResetCredentials() {
        assertFalse(isDatabaseResetCredentials("RESETDATABASE", "wrong"))
        assertFalse(isDatabaseResetCredentials("resetdatabase", "RESETDATABASE"))
    }
}
