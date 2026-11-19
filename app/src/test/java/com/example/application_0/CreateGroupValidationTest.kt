package com.example.application_0

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CreateGroupValidationTest {
    @Test
    fun reportsMissingFields() {
        assertEquals(
            "Missing group name.",
            groupCreationValidationMessage("", "")
        )
    }

    @Test
    fun acceptsCompleteDetails() {
        assertNull(groupCreationValidationMessage("Friday dinner", "Dinner"))
    }

    @Test
    fun acceptsMissingOptionalType() {
        assertNull(groupCreationValidationMessage("Friday dinner", ""))
    }
}
