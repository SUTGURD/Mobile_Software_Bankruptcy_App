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
): Double = sumOutstandingDebts(expenses, expenseSplits, payments, targetDebtorId = personId) { gId, debtorId, _ ->
    gId == groupId && debtorId == personId
}

fun amountPersonIsOwedInGroup(
    personId: String,
    groupId: String,
    expenses: List<Expense>,
    expenseSplits: List<ExpenseSplit>,
    payments: List<Payment>
): Double = sumOutstandingDebts(expenses, expenseSplits, payments, targetCreditorId = personId) { gId, _, creditorId ->
    gId == groupId && creditorId == personId
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

    return sumOutstandingDebts(
        expenses,
        expenseSplits,
        payments,
        targetDebtorId = personId,
        targetCreditorId = otherPersonId
    ) { gId, debtorId, creditorId ->
        gId in groupIdsInCurrency &&
            debtorId == personId &&
            creditorId == otherPersonId
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

    return sumOutstandingDebts(expenses, expenseSplits, payments, targetDebtorId = personId) { gId, debtorId, _ ->
        gId in groupIdsInCurrency && debtorId == personId
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

    return sumOutstandingDebts(expenses, expenseSplits, payments, targetCreditorId = personId) { gId, _, creditorId ->
        gId in groupIdsInCurrency && creditorId == personId
    }
}

private fun sumOutstandingDebts(
    expenses: List<Expense>,
    expenseSplits: List<ExpenseSplit>,
    payments: List<Payment>,
    targetDebtorId: String? = null,
    targetCreditorId: String? = null,
    matches: (groupId: String, debtorId: String, creditorId: String) -> Boolean
): Double {
    if (expenseSplits.isEmpty()) return 0.0

    val expensesById = expenses.associateBy { it.id }
    val settledSplitIds = if (payments.isEmpty()) {
        emptySet()
    } else {
        val splitsById = expenseSplits.associateBy { it.id }
        payments.mapNotNull { payment ->
            val splitId = payment.expenseSplitId ?: return@mapNotNull null
            val split = splitsById[splitId] ?: return@mapNotNull null
            val expense = expensesById[split.expenseId] ?: return@mapNotNull null

            splitId.takeIf {
                payment.groupId == expense.groupId &&
                    payment.paidBy == split.userId &&
                    payment.paidTo == expense.paidBy
            }
        }.toSet()
    }

    var total = 0.0
    for (i in expenseSplits.indices) {
        val split = expenseSplits[i]
        if (targetDebtorId != null && split.userId != targetDebtorId) {
            continue
        }

        val expense = expensesById[split.expenseId] ?: continue
        if (targetCreditorId != null && expense.paidBy != targetCreditorId) {
            continue
        }
        if (split.id in settledSplitIds || split.userId == expense.paidBy) {
            continue
        }

        if (matches(expense.groupId, split.userId, expense.paidBy)) {
            total += split.amount
        }
    }

    return BigDecimal.valueOf(total).toDouble()
}
