package data.database

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import data.DummyData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import types.Expense
import types.ExpenseSplit
import types.Group
import types.GroupInvitation
import types.GroupMember
import types.Notification
import types.Payment
import types.Reminder
import types.User

@Database(
    entities = [
        UserEntity::class,
        GroupEntity::class,
        GroupMemberEntity::class,
        GroupInvitationEntity::class,
        ExpenseEntity::class,
        ExpenseSplitEntity::class,
        PaymentEntity::class,
        ReminderEntity::class,
        NotificationEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bankruptcy.db"
                ).addCallback(DatabaseSeedCallback(context.applicationContext))
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { instance = it }
            }
    }
}

private class DatabaseSeedCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getInstance(context)
            val dao = database.appDao()
            database.withTransaction {
                dao.insertUsers(DummyData.users.map { it.toEntity() })
                dao.insertGroups(DummyData.groups.map { it.toEntity() })
                dao.insertGroupMembers(DummyData.groupMembers.map { it.toEntity() })
                dao.insertGroupInvitations(DummyData.groupInvitations.map { it.toEntity() })
                dao.insertExpenses(DummyData.expenses.map { it.toEntity() })
                dao.insertExpenseSplits(DummyData.expenseSplits.map { it.toEntity() })
                dao.insertPayments(DummyData.payments.map { it.toEntity() })
                dao.insertReminders(DummyData.reminders.map { it.toEntity() })
                dao.insertNotifications(DummyData.notifications.map { it.toEntity() })
            }
        }
    }
}

class RoomDatabaseOperations(private val database: AppDatabase) : DatabaseOperations {
    private val dao: AppDao = database.appDao()

    override suspend fun createExpense(
        expense: Expense,
        splits: List<ExpenseSplit>,
        actorUserId: String
    ): ExpenseCreationResult = try {
        database.withTransaction {
            if (dao.getGroupById(expense.groupId) == null) {
                return@withTransaction ExpenseCreationResult.GROUP_NOT_FOUND
            }
            val memberships = dao.getGroupMembers()
                .filter { it.groupId == expense.groupId }
                .map { it.userId }
                .toSet()
            if (actorUserId !in memberships) {
                return@withTransaction ExpenseCreationResult.NOT_GROUP_MEMBER
            }

            val amountMinorUnits = expense.amount.toMinorUnits()
                ?: return@withTransaction ExpenseCreationResult.INVALID_EXPENSE
            val splitMinorUnits = splits.mapNotNull { it.amount.toMinorUnits() }
            val splitMemberIds = splits.map { it.userId }.toSet()
            if (amountMinorUnits <= 0 ||
                expense.paidBy != actorUserId ||
                expense.authorId != actorUserId ||
                splits.isEmpty() ||
                splitMinorUnits.size != splits.size ||
                splitMinorUnits.any { it < 0 } ||
                splitMemberIds != memberships ||
                splits.any {
                    it.expenseId != expense.id ||
                        it.userId !in memberships
                } ||
                splits.map { it.userId }.distinct().size != splits.size
            ) {
                return@withTransaction ExpenseCreationResult.INVALID_EXPENSE
            }

            val splitTotal = try {
                splitMinorUnits.fold(0L) { total, amount ->
                    Math.addExact(total, amount)
                }
            } catch (_: ArithmeticException) {
                return@withTransaction ExpenseCreationResult.INVALID_EXPENSE
            }
            if (splitTotal != amountMinorUnits) {
                return@withTransaction ExpenseCreationResult.INVALID_EXPENSE
            }

            dao.insertExpense(expense.toEntity())
            dao.insertExpenseSplitsForCreation(splits.map { it.toEntity() })
            ExpenseCreationResult.CREATED
        }
    } catch (_: Exception) {
        ExpenseCreationResult.FAILURE
    }

    override suspend fun createGroup(group: Group, member: GroupMember): Boolean =
        try {
            database.withTransaction {
                dao.insertGroups(listOf(group.toEntity()))
                dao.insertGroupMembers(listOf(member.toEntity()))
            }
            true
        } catch (_: Exception) {
            false
        }

    override suspend fun createGroupInvitation(
        invitation: GroupInvitation,
        notification: Notification
    ): MembershipMutationResult = database.withTransaction {
        val group = dao.getGroupById(invitation.groupId)
            ?: return@withTransaction MembershipMutationResult.NOT_FOUND
        val inviter = dao.getGroupMember(group.id, invitation.invitedByUserId)
            ?: return@withTransaction MembershipMutationResult.NOT_ALLOWED
        if (inviter.role != types.MemberRole.ADMIN && !group.allowAllMembersToAdd) {
            return@withTransaction MembershipMutationResult.NOT_ALLOWED
        }

        val inviteeById = invitation.inviteeUserId?.let { dao.getUserById(it) }
        val invitee = inviteeById ?: dao.getUserByEmail(invitation.inviteeEmail)
            ?: return@withTransaction MembershipMutationResult.INVALID_INVITEE
        if (!invitee.email.equals(invitation.inviteeEmail, ignoreCase = true) ||
            invitee.id == invitation.invitedByUserId
        ) {
            return@withTransaction MembershipMutationResult.INVALID_INVITEE
        }
        if (dao.getGroupMember(group.id, invitee.id) != null) {
            return@withTransaction MembershipMutationResult.ALREADY_MEMBER
        }
        if (dao.getGroupInvitation(group.id, invitee.email) != null) {
            return@withTransaction MembershipMutationResult.ALREADY_INVITED
        }

        val storedInvitation = invitation.copy(
            inviteeName = invitee.name,
            inviteeEmail = invitee.email,
            inviteeUserId = invitee.id
        )
        val storedNotification = notification.copy(
            userId = invitee.id,
            type = types.NotificationType.INVITATIONS,
            relatedEntityId = storedInvitation.id
        )
        dao.insertGroupInvitation(storedInvitation.toEntity())
        dao.insertNotification(storedNotification.toEntity())
        MembershipMutationResult.SUCCESS
    }

    override suspend fun resolveGroupInvitation(
        invitationId: String,
        userId: String,
        accept: Boolean
    ): MembershipMutationResult = database.withTransaction {
        val storedInvitation = dao.getGroupInvitationById(invitationId)
            ?: return@withTransaction MembershipMutationResult.NOT_FOUND
        val user = dao.getUserById(userId)
            ?: return@withTransaction MembershipMutationResult.NOT_ALLOWED
        val invitation = storedInvitation.toModel()
        if (invitation.inviteeUserId != userId &&
            !invitation.inviteeEmail.equals(user.email, ignoreCase = true)
        ) {
            return@withTransaction MembershipMutationResult.NOT_ALLOWED
        }

        val group = dao.getGroupById(invitation.groupId)
            ?: return@withTransaction MembershipMutationResult.NOT_FOUND
        if (accept && dao.getGroupMember(group.id, userId) != null) {
            return@withTransaction MembershipMutationResult.ALREADY_MEMBER
        }

        if (accept) {
            val now = java.time.LocalDateTime.now()
            dao.insertGroupMember(
                GroupMember(
                    groupId = group.id,
                    userId = userId,
                    role = types.MemberRole.MEMBER,
                    joinedAt = now
                ).toEntity()
            )
            val members = dao.getGroupMembers().filter { it.groupId == group.id }
            val notifications = members.mapNotNull { member ->
                val recipient = dao.getUserById(member.userId) ?: return@mapNotNull null
                val isNewMember = recipient.id == userId
                if (!isNewMember &&
                    (!recipient.notificationsEnabled ||
                        !recipient.notificationSettings.groupActivity)
                ) {
                    return@mapNotNull null
                }
                Notification(
                    id = "notification-group-joined-${group.id}-${recipient.id}-${invitation.id}",
                    userId = recipient.id,
                    type = types.NotificationType.GROUP,
                    message = if (isNewMember) {
                        "You joined ${group.name}."
                    } else {
                        "${user.name} joined ${group.name}."
                    },
                    createdAt = now,
                    read = false,
                    relatedEntityId = group.id
                ).toEntity()
            }
            dao.insertNotifications(notifications)
        }
        dao.deleteGroupInvitation(invitationId)
        dao.deleteInvitationNotifications(invitationId)
        MembershipMutationResult.SUCCESS
    }

    override suspend fun removeGroupInvitation(
        invitationId: String,
        actorUserId: String
    ): MembershipMutationResult = database.withTransaction {
        val storedInvitation = dao.getGroupInvitationById(invitationId)
            ?: return@withTransaction MembershipMutationResult.NOT_FOUND
        val invitation = storedInvitation.toModel()
        val group = dao.getGroupById(invitation.groupId)
            ?: return@withTransaction MembershipMutationResult.NOT_FOUND
        val actor = dao.getGroupMember(group.id, actorUserId)
            ?: return@withTransaction MembershipMutationResult.NOT_ALLOWED
        if (actor.role != types.MemberRole.ADMIN &&
            invitation.invitedByUserId != actorUserId
        ) {
            return@withTransaction MembershipMutationResult.NOT_ALLOWED
        }
        dao.deleteGroupInvitation(invitationId)
        dao.deleteInvitationNotifications(invitationId)
        MembershipMutationResult.SUCCESS
    }

    override suspend fun removeGroupMember(
        groupId: String,
        memberUserId: String,
        actorUserId: String
    ): MembershipMutationResult = database.withTransaction {
        val group = dao.getGroupById(groupId)
            ?: return@withTransaction MembershipMutationResult.NOT_FOUND
        val actor = dao.getGroupMember(groupId, actorUserId)
            ?: return@withTransaction MembershipMutationResult.NOT_ALLOWED
        if (actor.role != types.MemberRole.ADMIN && !group.allowAllMembersToRemove) {
            return@withTransaction MembershipMutationResult.NOT_ALLOWED
        }
        if (memberUserId == actorUserId ||
            dao.getGroupMember(groupId, memberUserId) == null
        ) {
            return@withTransaction MembershipMutationResult.NOT_ALLOWED
        }
        dao.deleteGroupMember(groupId, memberUserId)
        MembershipMutationResult.SUCCESS
    }

    override suspend fun resetDatabaseToDummyData(): Boolean =
        try {
            database.withTransaction {
                dao.clearNotifications()
                dao.clearReminders()
                dao.clearPayments()
                dao.clearExpenseSplits()
                dao.clearExpenses()
                dao.clearGroupInvitations()
                dao.clearGroupMembers()
                dao.clearGroups()
                dao.clearUsers()
                dao.insertUsers(DummyData.users.map { it.toEntity() })
                dao.insertGroups(DummyData.groups.map { it.toEntity() })
                dao.insertGroupMembers(DummyData.groupMembers.map { it.toEntity() })
                dao.insertGroupInvitations(DummyData.groupInvitations.map { it.toEntity() })
                dao.insertExpenses(DummyData.expenses.map { it.toEntity() })
                dao.insertExpenseSplits(DummyData.expenseSplits.map { it.toEntity() })
                dao.insertPayments(DummyData.payments.map { it.toEntity() })
                dao.insertReminders(DummyData.reminders.map { it.toEntity() })
                dao.insertNotifications(DummyData.notifications.map { it.toEntity() })
            }
            true
        } catch (_: Exception) {
            false
        }

    override suspend fun createUser(user: User): Boolean =
        try {
            dao.insertUser(user.toEntity()) != -1L
        } catch (_: SQLiteConstraintException) {
            false
        }

    override suspend fun updateUser(user: User): Boolean =
        try {
            dao.updateUser(user.toEntity())
            true
        } catch (_: SQLiteConstraintException) {
            false
        }

    override suspend fun getUsers(): List<User> =
        dao.getUsers().map { it.toModel() }

    override suspend fun getUserById(id: String): User? =
        dao.getUserById(id)?.toModel()

    override suspend fun getUserByEmail(email: String): User? =
        dao.getUserByEmail(email)?.toModel()

    override suspend fun getGroups(): List<Group> =
        dao.getGroups().map { it.toModel() }

    override suspend fun getGroupMembers(): List<GroupMember> =
        dao.getGroupMembers().map { it.toModel() }

    override suspend fun getGroupInvitations(): List<GroupInvitation> =
        dao.getGroupInvitations().map { it.toModel() }

    override suspend fun getExpenses(): List<Expense> =
        dao.getExpenses().map { it.toModel() }

    override suspend fun getExpenseSplits(): List<ExpenseSplit> =
        dao.getExpenseSplits().map { it.toModel() }

    override suspend fun getPayments(): List<Payment> =
        dao.getPayments().map { it.toModel() }

    override suspend fun getReminders(): List<Reminder> =
        dao.getReminders().map { it.toModel() }

    override suspend fun getNotifications(): List<Notification> =
        dao.getNotifications().map { it.toModel() }
}

private fun Double.toMinorUnits(): Long? =
    try {
        if (!isFinite()) null
        else BigDecimal.valueOf(this).movePointRight(2).longValueExact()
    } catch (_: ArithmeticException) {
        null
    }

object DatabaseProvider {
    private var operations: DatabaseOperations? = null

    fun initialise(context: Context) {
        if (operations == null) {
            operations = RoomDatabaseOperations(AppDatabase.getInstance(context))
        }
    }

    fun current(): DatabaseOperations =
        checkNotNull(operations) { "The database provider must be initialised before use." }
}
