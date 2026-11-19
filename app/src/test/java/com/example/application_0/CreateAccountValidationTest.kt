package com.example.application_0

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CreateAccountValidationTest {
    @Test
    fun reportsMissingFields() {
        assertEquals(
            "Missing full name, email address, password.",
            accountCreationValidationMessage("", "", "")
        )
    }

    @Test
    fun reportsShortPasswordAndInvalidEmail() {
        assertEquals(
            "Enter a valid email address.\nPassword must be at least 8 characters.",
            accountCreationValidationMessage("Emma", "invalid-email", "123")
        )
    }

    @Test
    fun acceptsCompleteValidDetails() {
        assertNull(
            accountCreationValidationMessage(
                "Emma Nielsen",
                "emma@example.com",
                "password123"
            )
        )
    }
}
