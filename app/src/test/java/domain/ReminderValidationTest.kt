package domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import types.Notification
import types.NotificationType
import types.Reminder
import java.time.LocalDateTime

class ReminderValidationTest {
    @Test
    fun reminderDataClassInitialisesCorrectly() {
        val now = LocalDateTime.now()
        val reminder = Reminder(
            id = "reminder-1",
            groupId = "group-1",
            description = "Payment reminder from Alice",
            authorId = "user-alice",
            expenseSplitId = null
        )
        val notification = Notification(
            id = "notification-1",
            userId = "user-bob",
            type = NotificationType.REMINDER,
            message = "Alice sent you a reminder about your outstanding balance.",
            createdAt = now,
            read = false,
            relatedEntityId = reminder.id
        )

        assertEquals("group-1", reminder.groupId)
        assertEquals("user-alice", reminder.authorId)
        assertEquals(NotificationType.REMINDER, notification.type)
        assertEquals("user-bob", notification.userId)
        assertNotNull(notification.createdAt)
    }
}
