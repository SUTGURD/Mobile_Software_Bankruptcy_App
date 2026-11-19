package com.example.application_0

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import data.database.AppDatabase
import data.database.ExpenseCreationResult
import data.database.RoomDatabaseOperations
import data.database.toEntity
import java.time.LocalDateTime
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import types.Expense
import types.ExpenseSplit
import types.Group
import types.GroupMember
import types.MemberRole

@RunWith(AndroidJUnit4::class)
class ExpensePersistenceTest {
    @Test
    fun expenseAndEqualSplitsAreSavedAndReloadedTogether() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val room = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val now = LocalDateTime.now()
            val groupId = "group-expense-test"
            val expenseId = "expense-test"
            val dao = room.appDao()
            dao.insertGroups(
                listOf(
                    Group(
                        id = groupId,
                        name = "Test group",
                        description = null,
                        authorId = "member-a",
                        createdAt = now,
                        allowAllMembersToAdd = true,
                        allowAllMembersToRemove = false,
                        currency = "DKK"
                    ).toEntity()
                )
            )
            dao.insertGroupMembers(
                listOf("member-a", "member-b", "member-c").map { userId ->
                    GroupMember(groupId, userId, MemberRole.MEMBER, now).toEntity()
                }
            )

            val expense = Expense(
                id = expenseId,
                groupId = groupId,
                amount = 10.00,
                description = "Test dinner",
                createdAt = now,
                paidBy = "member-a",
                receiptUri = "sample://expense-receipt",
                authorId = "member-a"
            )
            val splits = listOf(
                ExpenseSplit("split-a", expenseId, "member-a", 3.34),
                ExpenseSplit("split-b", expenseId, "member-b", 3.33),
                ExpenseSplit("split-c", expenseId, "member-c", 3.33)
            )

            assertEquals(
                ExpenseCreationResult.CREATED,
                RoomDatabaseOperations(room).createExpense(expense, splits, "member-a")
            )
            assertEquals(listOf(expense), RoomDatabaseOperations(room).getExpenses())
            assertEquals(splits, RoomDatabaseOperations(room).getExpenseSplits())

            val invalidExpense = expense.copy(id = "expense-invalid")
            assertEquals(
                ExpenseCreationResult.INVALID_EXPENSE,
                RoomDatabaseOperations(room).createExpense(
                    invalidExpense,
                    splits.map {
                        it.copy(
                            expenseId = invalidExpense.id,
                            amount = if (it.userId == "member-a") 3.33 else it.amount
                        )
                    },
                    "member-a"
                )
            )
            assertTrue(
                RoomDatabaseOperations(room).getExpenses().none {
                    it.id == invalidExpense.id
                }
            )
        } finally {
            room.close()
        }
    }
}
