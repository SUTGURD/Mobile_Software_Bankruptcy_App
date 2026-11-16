package domain

import org.junit.Assert.assertEquals
import org.junit.Test
import types.Expense
import types.ExpenseSplit
import types.Group
import types.Payment
import java.time.LocalDateTime

class DebtTest {
    private val groups = listOf(
        Group(
            id = "group-dkk-1",
            name = "Weekend",
            description = null,
            authorId = "alice",
            createdAt = LocalDateTime.of(2026, 11, 1, 10, 0),
            allowAllMembersToAdd = true,
            allowAllMembersToRemove = false,
            currency = "DKK"
        ),
        Group(
            id = "group-dkk-2",
            name = "Flat",
            description = null,
            authorId = "bob",
            createdAt = LocalDateTime.of(2026, 11, 1, 10, 0),
            allowAllMembersToAdd = true,
            allowAllMembersToRemove = false,
            currency = "DKK"
        ),
        Group(
            id = "group-eur",
            name = "Trip",
            description = null,
            authorId = "alice",
            createdAt = LocalDateTime.of(2026, 11, 1, 10, 0),
            allowAllMembersToAdd = true,
            allowAllMembersToRemove = false,
            currency = "EUR"
        )
    )

    private val expenses = listOf(
        expense("dinner", "group-dkk-1", "alice", 900.0),
        expense("groceries", "group-dkk-2", "bob", 240.0),
        expense("train", "group-eur", "alice", 100.0)
    )

    private val splits = listOf(
        ExpenseSplit("dinner-alice", "dinner", "alice", 300.0),
        ExpenseSplit("dinner-bob", "dinner", "bob", 300.0),
        ExpenseSplit("dinner-cara", "dinner", "cara", 300.0),
        ExpenseSplit("groceries-bob", "groceries", "bob", 120.0),
        ExpenseSplit("groceries-alice", "groceries", "alice", 120.0),
        ExpenseSplit("train-bob", "train", "bob", 50.0),
        ExpenseSplit("train-alice", "train", "alice", 50.0)
    )

    private val payments = listOf(
        Payment(
            id = "payment-dinner",
            groupId = "group-dkk-1",
            paidBy = "bob",
            paidTo = "alice",
            expenseSplitId = "dinner-bob",
            description = null,
            paidAt = LocalDateTime.of(2026, 11, 2, 12, 0)
        )
    )

    @Test
    fun amountPersonOwesInGroupSumsUnsettledSplits() {
        assertEquals(120.0, amountPersonOwesInGroup("alice", "group-dkk-2", expenses, splits, payments), 0.0)
        assertEquals(0.0, amountPersonOwesInGroup("bob", "group-dkk-1", expenses, splits, payments), 0.0)
    }

    @Test
    fun amountPersonIsOwedInGroupSumsUnsettledSplits() {
        assertEquals(300.0, amountPersonIsOwedInGroup("alice", "group-dkk-1", expenses, splits, payments), 0.0)
        assertEquals(120.0, amountPersonIsOwedInGroup("bob", "group-dkk-2", expenses, splits, payments), 0.0)
    }

    @Test
    fun amountPersonOwesAnotherPersonIncludesOnlySelectedCurrencyAndPair() {
        assertEquals(
            300.0,
            amountPersonOwesAnotherPerson("cara", "alice", "DKK", groups, expenses, splits, payments),
            0.0
        )
        assertEquals(
            0.0,
            amountPersonOwesAnotherPerson("bob", "alice", "DKK", groups, expenses, splits, payments),
            0.0
        )
    }

    @Test
    fun totalAmountPersonOwesSumsDebtAcrossGroupsInCurrency() {
        assertEquals(120.0, totalAmountPersonOwes("alice", "DKK", groups, expenses, splits, payments), 0.0)
        assertEquals(50.0, totalAmountPersonOwes("bob", "EUR", groups, expenses, splits, payments), 0.0)
    }

    @Test
    fun totalAmountPersonIsOwedSumsDebtAcrossGroupsInCurrency() {
        assertEquals(300.0, totalAmountPersonIsOwed("alice", "DKK", groups, expenses, splits, payments), 0.0)
        assertEquals(50.0, totalAmountPersonIsOwed("alice", "EUR", groups, expenses, splits, payments), 0.0)
    }

    private fun expense(id: String, groupId: String, paidBy: String, amount: Double) = Expense(
        id = id,
        groupId = groupId,
        amount = amount,
        description = null,
        createdAt = LocalDateTime.of(2026, 11, 1, 10, 0),
        paidBy = paidBy,
        receiptUri = null,
        authorId = paidBy
    )
}
