package data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AppDao {
    @Query("DELETE FROM notifications")
    suspend fun clearNotifications()

    @Query("DELETE FROM reminders")
    suspend fun clearReminders()

    @Query("DELETE FROM payments")
    suspend fun clearPayments()

    @Query("DELETE FROM expense_splits")
    suspend fun clearExpenseSplits()

    @Query("DELETE FROM expenses")
    suspend fun clearExpenses()

    @Query("DELETE FROM group_invitations")
    suspend fun clearGroupInvitations()

    @Query("DELETE FROM group_members")
    suspend fun clearGroupMembers()

    @Query("DELETE FROM groups")
    suspend fun clearGroups()

    @Query("DELETE FROM users")
    suspend fun clearUsers()

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<GroupEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupMembers(members: List<GroupMemberEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupInvitations(invitations: List<GroupInvitationEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertGroupInvitation(invitation: GroupInvitationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupMember(member: GroupMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseSplits(splits: List<ExpenseSplitEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertExpenseSplitsForCreation(splits: List<ExpenseSplitEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<PaymentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<ReminderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM users")
    suspend fun getUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE lower(email) = lower(:email)")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM groups")
    suspend fun getGroups(): List<GroupEntity>

    @Query("SELECT * FROM groups WHERE id = :id")
    suspend fun getGroupById(id: String): GroupEntity?

    @Query("SELECT * FROM group_members")
    suspend fun getGroupMembers(): List<GroupMemberEntity>

    @Query("SELECT * FROM group_members WHERE groupId = :groupId AND userId = :userId")
    suspend fun getGroupMember(groupId: String, userId: String): GroupMemberEntity?

    @Query("SELECT * FROM group_invitations")
    suspend fun getGroupInvitations(): List<GroupInvitationEntity>

    @Query("SELECT * FROM group_invitations WHERE id = :id")
    suspend fun getGroupInvitationById(id: String): GroupInvitationEntity?

    @Query(
        """
        SELECT * FROM group_invitations
        WHERE groupId = :groupId AND lower(inviteeEmail) = lower(:inviteeEmail)
        LIMIT 1
        """
    )
    suspend fun getGroupInvitation(groupId: String, inviteeEmail: String): GroupInvitationEntity?

    @Query("DELETE FROM group_invitations WHERE id = :id")
    suspend fun deleteGroupInvitation(id: String)

    @Query(
        "DELETE FROM notifications WHERE type = 'INVITATIONS' AND relatedEntityId = :invitationId"
    )
    suspend fun deleteInvitationNotifications(invitationId: String)

    @Query("DELETE FROM group_members WHERE groupId = :groupId AND userId = :userId")
    suspend fun deleteGroupMember(groupId: String, userId: String)

    @Query("SELECT * FROM expenses")
    suspend fun getExpenses(): List<ExpenseEntity>

    @Query("SELECT * FROM expense_splits")
    suspend fun getExpenseSplits(): List<ExpenseSplitEntity>

    @Query("SELECT * FROM payments")
    suspend fun getPayments(): List<PaymentEntity>

    @Query("SELECT * FROM reminders")
    suspend fun getReminders(): List<ReminderEntity>

    @Query("SELECT * FROM notifications")
    suspend fun getNotifications(): List<NotificationEntity>
}
