package domain

import org.junit.Assert.assertTrue
import org.junit.Test
import types.Expense
import types.ExpenseSplit
import types.Group
import types.Payment
import java.time.LocalDateTime
import kotlin.system.measureTimeMillis

class DebtPerformanceTest {
    @Test
    fun debtCalculationFor50MembersAnd1000ExpensesCompletesUnder50Milliseconds() {
        val groupId = "group-perf"
        val group = Group(
            id = groupId,
            name = "Performance test group",
            description = null,
            authorId = "user-0",
            createdAt = LocalDateTime.now(),
            allowAllMembersToAdd = true,
            allowAllMembersToRemove = false,
            currency = "DKK"
        )
        val members = (0 until 50).map { "user-$it" }
        val expenses = mutableListOf<Expense>()
        val splits = mutableListOf<ExpenseSplit>()

        for (i in 0 until 1000) {
            val expenseId = "expense-$i"
            val payer = members[i % members.size]
            expenses.add(
                Expense(
                    id = expenseId,
                    groupId = groupId,
                    amount = 500.0,
                    description = "Expense $i",
                    createdAt = LocalDateTime.now(),
                    paidBy = payer,
                    receiptUri = null,
                    authorId = payer
                )
            )
            for (m in 0 until 50) {
                splits.add(
                    ExpenseSplit(
                        id = "split-$i-$m",
                        expenseId = expenseId,
                        userId = members[m],
                        amount = 10.0
                    )
                )
            }
        }

        val payments = emptyList<Payment>()
        val groups = listOf(group)

        // JVM Warm-up to trigger JIT compilation before benchmarking
        amountPersonOwesInGroup("user-0", groupId, expenses, splits, payments)

        val durationMs = measureTimeMillis {
            val owes = amountPersonOwesInGroup("user-0", groupId, expenses, splits, payments)
            val isOwed = amountPersonIsOwedInGroup("user-0", groupId, expenses, splits, payments)
            val totalOwes = totalAmountPersonOwes("user-0", "DKK", groups, expenses, splits, payments)
            val totalIsOwed = totalAmountPersonIsOwed("user-0", "DKK", groups, expenses, splits, payments)
            val owesAnother = amountPersonOwesAnotherPerson("user-0", "user-1", "DKK", groups, expenses, splits, payments)

            assertTrue(owes >= 0.0)
            assertTrue(isOwed >= 0.0)
            assertTrue(totalOwes >= 0.0)
            assertTrue(totalIsOwed >= 0.0)
            assertTrue(owesAnother >= 0.0)
        }

        assertTrue("Debt calculation took $durationMs ms, which exceeds the 50 ms target", durationMs < 50)
    }
}
