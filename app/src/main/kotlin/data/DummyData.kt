package data

import types.Expense
import types.ExpenseSplit
import types.Group
import types.GroupInvitation
import types.GroupMember
import types.MemberRole
import types.Notification
import types.NotificationSettings
import types.NotificationType
import types.Payment
import types.Reminder
import types.User
import domain.hashPassword
import java.time.LocalDateTime

object DummyData {
    val users = listOf(
        User(
            id = "user-emma",
            name = "Emma Nielsen",
            email = "emma.nielsen@example.com",
            phoneNumber = "+45 20 11 22 33",
            createdAt = LocalDateTime.of(2026, 8, 12, 9, 0),
            updatedAt = LocalDateTime.of(2026, 11, 10, 14, 30),
            notificationsEnabled = true,
            notificationSettings = NotificationSettings(
                expenses = true,
                payments = true,
                reminders = true,
                groupActivity = true,
                invitations = true
            ),
            passwordHash = hashPassword("1231")
        ),
        User(
            id = "user-jonas",
            name = "Jonas Madsen",
            email = "jonas.madsen@example.com",
            phoneNumber = "+45 21 22 33 44",
            createdAt = LocalDateTime.of(2026, 8, 13, 10, 15),
            updatedAt = LocalDateTime.of(2026, 11, 8, 16, 0),
            notificationsEnabled = true,
            notificationSettings = NotificationSettings(
                expenses = true,
                payments = true,
                reminders = false,
                groupActivity = true,
                invitations = true
            ),
            passwordHash = hashPassword("1232")
        ),
        User(
            id = "user-freja",
            name = "Freja Larsen",
            email = "freja.larsen@example.com",
            phoneNumber = null,
            createdAt = LocalDateTime.of(2026, 8, 14, 11, 45),
            updatedAt = LocalDateTime.of(2026, 10, 30, 8, 20),
            notificationsEnabled = true,
            notificationSettings = NotificationSettings(
                expenses = true,
                payments = false,
                reminders = true,
                groupActivity = false,
                invitations = true
            ),
            passwordHash = hashPassword("1233")
        ),
        User(
            id = "user-mikkel",
            name = "Mikkel Sørensen",
            email = "mikkel.sorensen@example.com",
            phoneNumber = "+45 22 33 44 55",
            createdAt = LocalDateTime.of(2026, 9, 2, 12, 0),
            updatedAt = LocalDateTime.of(2026, 11, 2, 9, 10),
            notificationsEnabled = false,
            notificationSettings = NotificationSettings(
                expenses = true,
                payments = true,
                reminders = true,
                groupActivity = true,
                invitations = true
            ),
            passwordHash = hashPassword("1234")
        )
    )

    val groups = listOf(
        Group(
            id = "group-weekend",
            name = "Aarhus weekend",
            description = "Shared costs from our weekend in Aarhus.",
            authorId = "user-emma",
            createdAt = LocalDateTime.of(2026, 10, 20, 17, 30),
            allowAllMembersToAdd = true,
            allowAllMembersToRemove = false,
            currency = "DKK",
            type = "Trip"
        ),
        Group(
            id = "group-flat",
            name = "Nørrebro flat",
            description = "Household shopping and shared bills.",
            authorId = "user-jonas",
            createdAt = LocalDateTime.of(2026, 9, 5, 18, 0),
            allowAllMembersToAdd = true,
            allowAllMembersToRemove = true,
            currency = "DKK",
            type = "Home"
        )
    )

    val groupMembers = listOf(
        GroupMember("group-weekend", "user-emma", MemberRole.ADMIN, LocalDateTime.of(2026, 10, 20, 17, 30)),
        GroupMember("group-weekend", "user-jonas", MemberRole.MEMBER, LocalDateTime.of(2026, 10, 20, 17, 45)),
        GroupMember("group-weekend", "user-freja", MemberRole.MEMBER, LocalDateTime.of(2026, 10, 20, 18, 5)),
        GroupMember("group-flat", "user-jonas", MemberRole.ADMIN, LocalDateTime.of(2026, 9, 5, 18, 0)),
        GroupMember("group-flat", "user-mikkel", MemberRole.MEMBER, LocalDateTime.of(2026, 9, 6, 9, 20))
    )

    val groupInvitations = listOf(
        GroupInvitation(
            id = "invitation-weekend-jason",
            groupId = "group-weekend",
            inviteeName = "Jason Manden",
            inviteeEmail = "jason.manden@email.com",
            invitedByUserId = "user-emma",
            createdAt = LocalDateTime.of(2026, 11, 12, 14, 0)
        ),
        GroupInvitation(
            id = "invitation-flat-emil",
            groupId = "group-flat",
            inviteeName = "Emil Manden",
            inviteeEmail = "emil.manden@email.com",
            invitedByUserId = "user-jonas",
            createdAt = LocalDateTime.of(2026, 11, 13, 10, 30)
        ),
        GroupInvitation(
            id = "invitation-weekend-mikkel",
            groupId = "group-weekend",
            inviteeName = "Mikkel Sørensen",
            inviteeEmail = "mikkel.sorensen@example.com",
            invitedByUserId = "user-emma",
            createdAt = LocalDateTime.of(2026, 11, 14, 11, 0),
            inviteeUserId = "user-mikkel"
        )
    )

    val expenses = listOf(
        Expense(
            id = "expense-dinner",
            groupId = "group-weekend",
            amount = 900.0,
            description = "Dinner at the harbour",
            createdAt = LocalDateTime.of(2026, 11, 7, 19, 40),
            paidBy = "user-emma",
            receiptUri = "sample://receipts/expense-dinner",
            authorId = "user-emma"
        ),
        Expense(
            id = "expense-groceries",
            groupId = "group-flat",
            amount = 240.0,
            description = "Weekly groceries",
            createdAt = LocalDateTime.of(2026, 11, 9, 17, 15),
            paidBy = "user-jonas",
            receiptUri = null,
            authorId = "user-jonas"
        ),
        Expense(
            id = "expense-taxi",
            groupId = "group-weekend",
            amount = 300.0,
            description = "Shared taxi",
            createdAt = LocalDateTime.of(2026, 11, 8, 22, 15),
            paidBy = "user-freja",
            receiptUri = null,
            authorId = "user-freja"
        )
    )

    val expenseSplits = listOf(
        ExpenseSplit("split-dinner-emma", "expense-dinner", "user-emma", 300.0),
        ExpenseSplit("split-dinner-jonas", "expense-dinner", "user-jonas", 300.0),
        ExpenseSplit("split-dinner-freja", "expense-dinner", "user-freja", 300.0),
        ExpenseSplit("split-taxi-freja", "expense-taxi", "user-freja", 150.0),
        ExpenseSplit("split-taxi-jonas", "expense-taxi", "user-jonas", 150.0),
        ExpenseSplit("split-groceries-jonas", "expense-groceries", "user-jonas", 120.0),
        ExpenseSplit("split-groceries-mikkel", "expense-groceries", "user-mikkel", 120.0)
    )

    val payments = listOf(
        Payment(
            id = "payment-dinner-jonas",
            groupId = "group-weekend",
            paidBy = "user-jonas",
            paidTo = "user-emma",
            expenseSplitId = "split-dinner-jonas",
            description = "Dinner share",
            paidAt = LocalDateTime.of(2026, 11, 8, 10, 5)
        )
    )

    val reminders = listOf(
        Reminder(
            id = "reminder-dinner-freja",
            groupId = "group-weekend",
            description = "Freja still owes Emma for dinner.",
            authorId = "user-emma",
            expenseSplitId = "split-dinner-freja"
        ),
        Reminder(
            id = "reminder-groceries-mikkel",
            groupId = "group-flat",
            description = "Settle your share of the weekly groceries.",
            authorId = "user-jonas",
            expenseSplitId = "split-groceries-mikkel"
        )
    )

    val notifications = listOf(
        Notification(
            id = "notification-dinner",
            userId = "user-jonas",
            type = NotificationType.EXPENSE,
            message = "Emma added a DKK 900.00 dinner expense.",
            createdAt = LocalDateTime.of(2026, 11, 7, 19, 45),
            read = true,
            relatedEntityId = "expense-dinner"
        ),
        Notification(
            id = "notification-payment",
            userId = "user-emma",
            type = NotificationType.PAYMENT,
            message = "Jonas paid his dinner share.",
            createdAt = LocalDateTime.of(2026, 11, 8, 10, 5),
            read = false,
            relatedEntityId = "payment-dinner-jonas"
        ),
        Notification(
            id = "notification-reminder",
            userId = "user-freja",
            type = NotificationType.REMINDER,
            message = "Emma sent you a reminder about the dinner expense.",
            createdAt = LocalDateTime.of(2026, 11, 8, 12, 0),
            read = false,
            relatedEntityId = "reminder-dinner-freja"
        ),
        Notification(
            id = "notification-group",
            userId = "user-mikkel",
            type = NotificationType.GROUP,
            message = "Jonas added you to Nørrebro flat.",
            createdAt = LocalDateTime.of(2026, 9, 6, 9, 20),
            read = true,
            relatedEntityId = "group-flat"
        ),
        Notification(
            id = "notification-invitation-weekend-mikkel",
            userId = "user-mikkel",
            type = NotificationType.INVITATIONS,
            message = "Emma Nielsen invited you to join Aarhus weekend.",
            createdAt = LocalDateTime.of(2026, 11, 14, 11, 0),
            read = false,
            relatedEntityId = "invitation-weekend-mikkel"
        )
    )
}
