package types

import java.time.LocalDateTime

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val profileImageUri: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val notificationsEnabled: Boolean,
    val notificationSettings: NotificationSettings,
    val passwordHash: String
)

data class NotificationSettings(
    val expenses: Boolean,
    val payments: Boolean,
    val reminders: Boolean,
    val groupActivity: Boolean,
    val invitations: Boolean = true
)

data class  Group(
    val id: String,
    val name: String,
    val description : String?,
    val authorId: String,
    val createdAt: LocalDateTime,
    val allowAllMembersToAdd: Boolean,
    val allowAllMembersToRemove: Boolean,
    val currency: String,
    val type: String? = null
)

data class GroupMember(
    val groupId: String,
    val userId: String,
    val role: MemberRole,
    val joinedAt: LocalDateTime
)

data class GroupInvitation(
    val id: String,
    val groupId: String,
    val inviteeName: String,
    val inviteeEmail: String,
    val invitedByUserId: String,
    val createdAt: LocalDateTime,
    val inviteeUserId: String? = null
)

enum class MemberRole {
    ADMIN,
    MEMBER
}

data class Expense(
    val id: String,
    val groupId: String,
    val amount: Double,
    val description: String?,
    val createdAt: LocalDateTime,
    val paidBy: String,
    val receiptUri: String?,
    val authorId: String
)

data class ExpenseSplit(
    val id: String,
    val expenseId: String,
    val userId: String,
    val amount: Double
)

data class Payment(
    val id: String,
    val groupId: String,
    val paidBy: String,
    val paidTo: String,
    val expenseSplitId: String?,
    val description: String?,
    val paidAt: LocalDateTime
)

data class Reminder(
    val id: String,
    val groupId: String,
    val description: String?,
    val authorId: String,
    val expenseSplitId: String?
)

data class Notification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val message: String,
    val createdAt: LocalDateTime,
    val read: Boolean,
    val relatedEntityId: String?
)

enum class NotificationType {
    EXPENSE,
    PAYMENT,
    REMINDER,
    GROUP,
    INVITATIONS
}