package domain

import types.Expense
import types.ExpenseSplit
import types.Group
import types.Payment
import java.math.BigDecimal

fun amountPersonOwesInGroup(
    personId: String,
    groupId: String,
    expenses: List<Expense>,
    expenseSplits: List<ExpenseSplit>,
    payments: List<Payment>
): Double = sumOutstandingDebts(expenses, expenseSplits, payments) { debt ->
    debt.groupId == groupId && debt.debtorId == personId
}

fun amountPersonIsOwedInGroup(
    personId: String,
    groupId: String,
    expenses: List<Expense>,
    expenseSplits: List<ExpenseSplit>,
    payments: List<Payment>
): Double = sumOutstandingDebts(expenses, expenseSplits, payments) { debt ->
    debt.groupId == groupId && debt.creditorId == personId
}

fun amountPersonOwesAnotherPerson(
    personId: String,
    otherPersonId: String,
    currency: String,
    groups: List<Group>,
    expenses: List<Expense>,
    expenseSplits: List<ExpenseSplit>,
    payments: List<Payment>
): Double {
    val groupIdsInCurrency = groups.asSequence()
        .filter { it.currency == currency }
        .map { it.id }
        .toSet()

    return sumOutstandingDebts(expenses, expenseSplits, payments) { debt ->
        debt.groupId in groupIdsInCurrency &&
            debt.debtorId == personId &&
            debt.creditorId == otherPersonId
    }
}

fun totalAmountPersonOwes(
    personId: String,
    currency: String,
    groups: List<Group>,
    expenses: List<Expense>,
    expenseSplits: List<ExpenseSplit>,
    payments: List<Payment>
): Double {
    val groupIdsInCurrency = groups.asSequence()
        .filter { it.currency == currency }
        .map { it.id }
        .toSet()

    return sumOutstandingDebts(expenses, expenseSplits, payments) { debt ->
        debt.groupId in groupIdsInCurrency && debt.debtorId == personId
    }
}

fun totalAmountPersonIsOwed(
    personId: String,
    currency: String,
    groups: List<Group>,
    expenses: List<Expense>,
    expenseSplits: List<ExpenseSplit>,
    payments: List<Payment>
): Double {
    val groupIdsInCurrency = groups.asSequence()
        .filter { it.currency == currency }
        .map { it.id }
        .toSet()

    return sumOutstandingDebts(expenses, expenseSplits, payments) { debt ->
        debt.groupId in groupIdsInCurrency && debt.creditorId == personId
    }
}

private fun sumOutstandingDebts(
    expenses: List<Expense>,
    expenseSplits: List<ExpenseSplit>,
    payments: List<Payment>,
    matches: (Debt) -> Boolean
): Double {
    val expensesById = expenses.associateBy { it.id }
    val splitsById = expenseSplits.associateBy { it.id }
    // Payment has no amount field, so a correctly linked payment settles its full split.
    val settledSplitIds = payments.mapNotNull { payment ->
        val splitId = payment.expenseSplitId ?: return@mapNotNull null
        val split = splitsById[splitId] ?: return@mapNotNull null
        val expense = expensesById[split.expenseId] ?: return@mapNotNull null

        splitId.takeIf {
            payment.groupId == expense.groupId &&
                payment.paidBy == split.userId &&
                payment.paidTo == expense.paidBy
        }
    }.toSet()

    val total = expenseSplits.fold(BigDecimal.ZERO) { sum, split ->
        val expense = expensesById[split.expenseId]
            ?: return@fold sum
        if (split.id in settledSplitIds || split.userId == expense.paidBy) {
            return@fold sum
        }

        val debt = Debt(
            groupId = expense.groupId,
            debtorId = split.userId,
            creditorId = expense.paidBy,
            amount = BigDecimal.valueOf(split.amount)
        )
        if (matches(debt)) sum.add(debt.amount) else sum
    }

    return total.toDouble()
}

private data class Debt(
    val groupId: String,
    val debtorId: String,
    val creditorId: String,
    val amount: BigDecimal
)
