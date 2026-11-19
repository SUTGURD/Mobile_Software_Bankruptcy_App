package com.example.application_0

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExpenseCreationTest {
    @Test
    fun amountMustBePositiveAndHaveAtMostTwoDecimalPlaces() {
        assertEquals(1234L, amountToMinorUnits("12.34"))
        assertEquals(100L, amountToMinorUnits("1"))
        assertNull(amountToMinorUnits("0"))
        assertNull(amountToMinorUnits("-1.00"))
        assertNull(amountToMinorUnits("1.234"))
        assertNull(amountToMinorUnits("not an amount"))
    }

    @Test
    fun equalSharesAllocateEveryMinorUnitExactlyOnce() {
        assertEquals(
            listOf("member-a" to 334L, "member-b" to 333L, "member-c" to 333L),
            allocateEqualShares(1000L, listOf("member-c", "member-a", "member-b"))
        )
        assertEquals(
            1000L,
            checkNotNull(
                allocateEqualShares(1000L, listOf("member-a", "member-b", "member-c"))
            ).sumOf { it.second }
        )
        assertNull(allocateEqualShares(100L, emptyList()))
        assertNull(allocateEqualShares(100L, listOf("member-a", "member-a")))
    }
}
