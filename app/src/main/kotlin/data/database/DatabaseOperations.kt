package data.database

import types.Expense
import types.ExpenseSplit
import types.Group
import types.GroupInvitation
import types.GroupMember
import types.Notification
import types.Payment
import types.Reminder
import types.User

enum class MembershipMutationResult {
    SUCCESS,
    NOT_FOUND,
    NOT_ALLOWED,
    INVALID_INVITEE,
    ALREADY_MEMBER,
    ALREADY_INVITED,
    FAILURE
}

enum class ExpenseCreationResult {
    CREATED,
    GROUP_NOT_FOUND,
    NOT_GROUP_MEMBER,
    INVALID_EXPENSE,
    FAILURE
}

interface DatabaseOperations {
    suspend fun createExpense(
        expense: Expense,
        splits: List<ExpenseSplit>,
        actorUserId: String
    ): ExpenseCreationResult
    suspend fun createGroup(group: Group, member: GroupMember): Boolean
    suspend fun createGroupInvitation(
        invitation: GroupInvitation,
        notification: Notification
    ): MembershipMutationResult
    suspend fun resolveGroupInvitation(
        invitationId: String,
        userId: String,
        accept: Boolean
    ): MembershipMutationResult
    suspend fun removeGroupInvitation(
        invitationId: String,
        actorUserId: String
    ): MembershipMutationResult
    suspend fun removeGroupMember(
        groupId: String,
        memberUserId: String,
        actorUserId: String
    ): MembershipMutationResult
    suspend fun updateUser(user: User): Boolean
    suspend fun resetDatabaseToDummyData(): Boolean
    suspend fun createUser(user: User): Boolean
    suspend fun getUsers(): List<User>
    suspend fun getUserById(id: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun getGroups(): List<Group>
    suspend fun getGroupMembers(): List<GroupMember>
    suspend fun getGroupInvitations(): List<GroupInvitation>
    suspend fun getExpenses(): List<Expense>
    suspend fun getExpenseSplits(): List<ExpenseSplit>
    suspend fun getPayments(): List<Payment>
    suspend fun getReminders(): List<Reminder>
    suspend fun getNotifications(): List<Notification>
}
