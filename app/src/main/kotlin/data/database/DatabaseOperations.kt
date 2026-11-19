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

interface DatabaseOperations {
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
