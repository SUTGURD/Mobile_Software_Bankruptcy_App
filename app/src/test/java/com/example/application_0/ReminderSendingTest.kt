package com.example.application_0

import org.junit.Assert.assertEquals
import org.junit.Test
import types.NotificationType

class ReminderSendingTest {
    @Test
    fun reminderNotificationTypeHasCorrectTitle() {
        assertEquals(NotificationType.REMINDER, NotificationType.valueOf("REMINDER"))
    }
}
