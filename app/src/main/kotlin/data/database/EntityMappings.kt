package data.database

import types.Expense
import types.ExpenseSplit
import types.Group
import types.GroupInvitation
import types.GroupMember
import types.Notification
import types.NotificationSettings
import types.Payment
import types.Reminder
import types.User

fun User.toEntity() = UserEntity(
    id = id,
    name = name,
    email = email,
    phoneNumber = phoneNumber,
    profileImageUri = profileImageUri,
    createdAt = createdAt,
    updatedAt = updatedAt,
    notificationsEnabled = notificationsEnabled,
    notificationSettings = NotificationSettingsEntity(
        expenses = notificationSettings.expenses,
        payments = notificationSettings.payments,
        reminders = notificationSettings.reminders,
        groupActivity = notificationSettings.groupActivity,
        invitations = notificationSettings.invitations
    ),
    passwordHash = passwordHash
)

fun UserEntity.toModel() = User(
    id = id,
    name = name,
    email = email,
    phoneNumber = phoneNumber,
    profileImageUri = profileImageUri,
    createdAt = createdAt,
    updatedAt = updatedAt,
    notificationsEnabled = notificationsEnabled,
    notificationSettings = NotificationSettings(
        expenses = notificationSettings.expenses,
        payments = notificationSettings.payments,
        reminders = notificationSettings.reminders,
        groupActivity = notificationSettings.groupActivity,
        invitations = notificationSettings.invitations
    ),
    passwordHash = passwordHash
)

fun Group.toEntity() = GroupEntity(
    id, name, description, authorId, createdAt,
    allowAllMembersToAdd, allowAllMembersToRemove, currency, type
)

fun GroupEntity.toModel() = Group(
    id, name, description, authorId, createdAt,
    allowAllMembersToAdd, allowAllMembersToRemove, currency, type
)

fun GroupMember.toEntity() = GroupMemberEntity(groupId, userId, role, joinedAt)
fun GroupMemberEntity.toModel() = GroupMember(groupId, userId, role, joinedAt)

fun GroupInvitation.toEntity() =
    GroupInvitationEntity(
        id, groupId, inviteeName, inviteeEmail, invitedByUserId, createdAt, inviteeUserId
    )

fun GroupInvitationEntity.toModel() =
    GroupInvitation(
        id, groupId, inviteeName, inviteeEmail, invitedByUserId, createdAt, inviteeUserId
    )

fun Expense.toEntity() =
    ExpenseEntity(id, groupId, amount, description, createdAt, paidBy, receiptUri, authorId)

fun ExpenseEntity.toModel() =
    Expense(id, groupId, amount, description, createdAt, paidBy, receiptUri, authorId)

fun ExpenseSplit.toEntity() = ExpenseSplitEntity(id, expenseId, userId, amount)
fun ExpenseSplitEntity.toModel() = ExpenseSplit(id, expenseId, userId, amount)

fun Payment.toEntity() =
    PaymentEntity(id, groupId, paidBy, paidTo, expenseSplitId, description, paidAt)

fun PaymentEntity.toModel() =
    Payment(id, groupId, paidBy, paidTo, expenseSplitId, description, paidAt)

fun Reminder.toEntity() =
    ReminderEntity(id, groupId, description, authorId, expenseSplitId)

fun ReminderEntity.toModel() =
    Reminder(id, groupId, description, authorId, expenseSplitId)

fun Notification.toEntity() =
    NotificationEntity(id, userId, type, message, createdAt, read, relatedEntityId)

fun NotificationEntity.toModel() =
    Notification(id, userId, type, message, createdAt, read, relatedEntityId)
