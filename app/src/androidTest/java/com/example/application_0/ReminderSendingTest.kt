package com.example.application_0

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import data.database.AppDatabase
import data.database.DatabaseOperations
import data.database.RoomDatabaseOperations
import java.time.LocalDateTime
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import types.Notification
import types.NotificationType
import types.Reminder

@RunWith(AndroidJUnit4::class)
class ReminderSendingTest {
    @Test
    fun sendingReminderInsertsReminderAndNotification() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val room = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val operations: DatabaseOperations = RoomDatabaseOperations(room)
            val reminder = Reminder(
                id = "reminder-test-1",
                groupId = "group-1",
                description = "Payment reminder from Alice",
                authorId = "user-alice",
                expenseSplitId = null
            )
            val notification = Notification(
                id = "notification-test-1",
                userId = "user-bob",
                type = NotificationType.REMINDER,
                message = "Alice sent you a reminder about your outstanding balance.",
                createdAt = LocalDateTime.now(),
                read = false,
                relatedEntityId = reminder.id
            )

            val success = operations.sendReminder(reminder, notification)

            assertTrue(success)
            assertEquals(listOf(reminder), operations.getReminders())
            val userNotifications = operations.getNotifications().filter { it.userId == "user-bob" }
            assertEquals(1, userNotifications.size)
            assertEquals(NotificationType.REMINDER, userNotifications.single().type)
            assertEquals("Alice sent you a reminder about your outstanding balance.", userNotifications.single().message)
        } finally {
            room.close()
        }
    }
}
