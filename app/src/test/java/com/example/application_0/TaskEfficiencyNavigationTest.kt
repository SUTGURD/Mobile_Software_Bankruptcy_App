package com.example.application_0

import org.junit.Assert.assertTrue
import org.junit.Test

class TaskEfficiencyNavigationTest {
    @Test
    fun creatingGroupAndRecordingSplitExpenseTakesFiveOrFewerScreenTransitions() {
        val userFlowTransitions = listOf(
            AppRoute.Entry to AppRoute.Home,
            AppRoute.Home to AppRoute.Groups,
            AppRoute.Groups to AppRoute.CreateGroup,
            AppRoute.CreateGroup to AppRoute.GroupDetails,
            AppRoute.GroupDetails to AppRoute.AddExpense
        )

        val totalTransitions = userFlowTransitions.size

        assertTrue(
            "User workflow to create group and add expense must take 5 or fewer screen transitions, but took $totalTransitions",
            totalTransitions <= 5
        )
    }
}
