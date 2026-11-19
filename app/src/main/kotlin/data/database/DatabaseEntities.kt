package data.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import types.MemberRole
import types.NotificationType
import java.time.LocalDateTime

class DatabaseConverters {
    @TypeConverter
    fun localDateTimeToString(value: LocalDateTime?): String? = value?.toString()

    @TypeConverter
    fun stringToLocalDateTime(value: String?): LocalDateTime? =
        value?.let(LocalDateTime::parse)

    @TypeConverter
    fun memberRoleToString(value: MemberRole?): String? = value?.name

    @TypeConverter
    fun stringToMemberRole(value: String?): MemberRole? =
        value?.let(MemberRole::valueOf)

    @TypeConverter
    fun notificationTypeToString(value: NotificationType?): String? = value?.name

    @TypeConverter
    fun stringToNotificationType(value: String?): NotificationType? =
        value?.let(NotificationType::valueOf)
}

data class NotificationSettingsEntity(
    @ColumnInfo(name = "notifications_expenses") val expenses: Boolean,
    @ColumnInfo(name = "notifications_payments") val payments: Boolean,
    @ColumnInfo(name = "notifications_reminders") val reminders: Boolean,
    @ColumnInfo(name = "notifications_group_activity") val groupActivity: Boolean
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val profileImageUri: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val notificationsEnabled: Boolean,
    @Embedded(prefix = "notification_") val notificationSettings: NotificationSettingsEntity,
    val passwordHash: String
)

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val authorId: String,
    val createdAt: LocalDateTime,
    val allowAllMembersToAdd: Boolean,
    val allowAllMembersToRemove: Boolean,
    val currency: String,
    val type: String?
)

@Entity(tableName = "group_members", primaryKeys = ["groupId", "userId"])
data class GroupMemberEntity(
    val groupId: String,
    val userId: String,
    val role: MemberRole,
    val joinedAt: LocalDateTime
)

@Entity(tableName = "group_invitations")
data class GroupInvitationEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val inviteeName: String,
    val inviteeEmail: String,
    val invitedByUserId: String,
    val createdAt: LocalDateTime
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val amount: Double,
    val description: String?,
    val createdAt: LocalDateTime,
    val paidBy: String,
    val receiptUri: String?,
    val authorId: String
)

@Entity(tableName = "expense_splits")
data class ExpenseSplitEntity(
    @PrimaryKey val id: String,
    val expenseId: String,
    val userId: String,
    val amount: Double
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val paidBy: String,
    val paidTo: String,
    val expenseSplitId: String?,
    val description: String?,
    val paidAt: LocalDateTime
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val description: String?,
    val authorId: String,
    val expenseSplitId: String?
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: NotificationType,
    val message: String,
    val createdAt: LocalDateTime,
    val read: Boolean,
    val relatedEntityId: String?
)
